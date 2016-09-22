package com.pgs.spark.bigdata.processor.jobs.filterJobs;

import com.datastax.driver.mapping.annotations.Table;
import com.pgs.spark.bigdata.processor.domain.Classification;
import com.pgs.spark.bigdata.processor.domain.Document;
import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.domain.SearchCriteria;
import com.pgs.spark.bigdata.processor.jobs.SparkJob;
import com.pgs.spark.bigdata.processor.repository.SearchCriteriaRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.spark.ml.Estimator;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pgs.spark.bigdata.processor.utils.RowUtils.localDateFromRow;
import static com.pgs.spark.bigdata.processor.utils.RowUtils.uuidFromRow;

/**
 * The type Filter job.
 */
@Component
public class FilterJob extends SparkJob {

    private static final String CASSANDRA_FORMAT = "org.apache.spark.sql.cassandra";

    private static final String KEYSPACE_PROPERTY = "keyspace";

    private static final String TABLE_PROPERTY = "table";

    private static final String TEMP_TABLE_NAME = "document";

    private static final String DOCUMENT_TABLE_NAME = Document.class.getAnnotation(Table.class).name();

    @Value("${spring.data.cassandra.keyspaceName}")
    private String keyspace;

    @Autowired
    private SearchCriteriaRepository searchCriteriaRepository;

    public void run(final UUID searchProfileId) {
        executor.submit(() -> {
            final List<SearchCriteria> searchCriteria = searchCriteriaRepository.findBySearchProfileId(searchProfileId);
            final Column filterCondition = prepareFilterCondition(searchCriteria);

            final DataFrame df = sqlContext.read()
                    .format(CASSANDRA_FORMAT)
                    .option(KEYSPACE_PROPERTY, keyspace)
                    .option(TABLE_PROPERTY, DOCUMENT_TABLE_NAME)
                    .load();
            df.registerTempTable(TEMP_TABLE_NAME);
            final List<Row> results = df.sqlContext()
                    .sql("select id, content, url, creation_date, tags, classification from document")
                    .filter(filterCondition)
                    .select("id", "content", "url", "creation_date", "tags", "classification")
                    .collectAsList();
            insertResultsIfNotExist(results, searchProfileId);
        });
    }

    @Override
    protected Estimator<?> getConfiguration() {
        return null;
    }

    private Column prepareFilterCondition(final List<SearchCriteria> searchCriteriaList) {
        final Column content = new Column("content");
        Column aggregateCondition = null;
        for (SearchCriteria searchCriteria : searchCriteriaList) {
            Column condition = containsWholeWord(content, searchCriteria.getKeyWord());
            if (StringUtils.isNotEmpty(searchCriteria.getMustHaveWord())) {
                condition = condition.and(containsWholeWord(content, searchCriteria.getMustHaveWord()));
            }
            if (StringUtils.isNotEmpty(searchCriteria.getExcludedWord())) {
                condition = condition.and(functions.not(containsWholeWord(content, searchCriteria.getExcludedWord())));
            }
            aggregateCondition = (aggregateCondition == null) ? condition : aggregateCondition.and(condition);
        }
        return aggregateCondition;
    }

    private Column containsWholeWord(final Column content, final String word) {
        return content.contains(word);
    }

    private void insertResultsIfNotExist(final List<Row> rows, final UUID searchProfileId) {
        final List<UUID> documentIds = rows.stream()
                .map(row -> uuidFromRow(row, 0))
                .collect(Collectors.toList());
        final List<UUID> resultIds = resultRepository.findByDocumentIdAndSearchProfileId(documentIds, searchProfileId)
                .stream()
                .map(Result::getId)
                .collect(Collectors.toList());
        rows.stream()
                .filter(row -> !resultIds.contains(uuidFromRow(row, 0)))
                .forEach(row -> {
                    final UUID documentId = uuidFromRow(row, 0);
                    final Result result;
                    if (row.getString(5).isEmpty()) {
                        result = Result.unclassifiedResult(searchProfileId, documentId, row.getString(2), row.getString(1), localDateFromRow(row, 3), row.getString(4));
                    } else {
                        result = Result.classifiedResult(searchProfileId, documentId, row.getString(2), row.getString(1), localDateFromRow(row, 3), row.getString(4), Classification.valueOf(row.getString(5)));
                    }
                    resultRepository.save(result);
                });
    }
}
