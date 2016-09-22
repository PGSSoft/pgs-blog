package com.pgs.spark.bigdata.processor.repository;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.pgs.spark.bigdata.processor.domain.Classification;
import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.domain.Tag;
import com.pgs.spark.bigdata.processor.dto.ResultPKDTO;
import com.pgs.spark.bigdata.processor.repository.util.PageRequest;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.StreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.fcall;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;
import static com.datastax.driver.core.querybuilder.QueryBuilder.token;
import static com.datastax.driver.core.querybuilder.QueryBuilder.update;

/**
 * The type Result repository.
 */
@Repository
public class ResultRepository extends AbstractRepository<Result> {

    @Autowired
    private SQLContext sqlContext;

    @Autowired
    private StreamingContext sc;

    @Autowired
    private TagRepository tagRepository;

    /**
     * Find results by document id and search profile id.
     *
     * @param documentId      the document id
     * @param searchProfileId the search profile id
     * @return the list
     */
    public List<Result> findByDocumentIdAndSearchProfileId(UUID documentId, UUID searchProfileId) {
        return getList(select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("document_id", documentId))
                .and(eq("search_profile_id", searchProfileId)));
    }

    /**
     * Find results by document id and search profile id.
     *
     * @param documentIds     the document ids
     * @param searchProfileId the search profile id
     * @return the list
     */
    public synchronized List<Result> findByDocumentIdAndSearchProfileId(List<UUID> documentIds, UUID searchProfileId) {
        return getList(select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(in("document_id", documentIds))
                .and(eq("search_profile_id", searchProfileId)));
    }


    /**
     * Find results to training by search profile.
     *
     * @param searchProfileId the search profile id
     * @return the list
     */
    public List<Result> findBySearchProfileToTraining(UUID searchProfileId) {
        return getList(select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId))
                .and(eq("is_training_data", true)));
    }

    /**
     * Find results by search profile to estimation.
     *
     * @param searchProfileId the search profile id
     * @param pageRequest     the page request
     * @return the list
     */
    public synchronized List<Result> findBySearchProfileToEstimating(UUID searchProfileId, PageRequest pageRequest) {
        final Where statement = select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId))
                .and(eq("is_training_data", false));

        if (pageRequest.hasOffset()) {
            statement.and(gt(token("id"), fcall("token", UUID.fromString(pageRequest.getOffsetId()))));
        }

        statement.limit(pageRequest.getLimit());

        return getList(statement);
    }

    /**
     * Update classification.
     *
     * @param uuids          the uuids
     * @param classification the classification
     */
    public void updateClassification(List<ResultPKDTO> uuids, Classification classification) {
        uuids.forEach(resultPKDTO -> {
            final Statement update = update(getCassandraTableName())
                    .with(set("classification", classification))
                    .where(eq("id", resultPKDTO.getResultId()))
                    .and(eq("document_id", resultPKDTO.getDocumentId()))
                    .and(eq("search_profile_id", resultPKDTO.getSearchProfileId()));
            session.execute(update);
        });
    }

    @Override
    protected Class<Result> getEntityClass() {
        return Result.class;
    }

    /**
     * Gets results by parent tag.
     *
     * @param tag             the tag
     * @param searchProfileId the search profile id
     * @return the results by parent tag
     */
    public List<Result> getResultsByParentTag(final Tag tag, final UUID searchProfileId) {
        final List<UUID> documentsIds = tagRepository.getChildTags(tag)
                .stream().map(Tag::getDocumentId).collect(Collectors.toList());

        return findByDocumentIdAndSearchProfileId(documentsIds, searchProfileId);
    }

    /**
     * Gets results by tag.
     *
     * @param tag             the tag
     * @param searchProfileId the search profile id
     * @return the results by tag
     */
    public List<Result> getResultsByTag(final Tag tag, final UUID searchProfileId) {
        return findByDocumentIdAndSearchProfileId(tag.getDocumentId(), searchProfileId);
    }
}
