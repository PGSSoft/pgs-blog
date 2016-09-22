package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Statement;
import com.pgs.spark.bigdata.algorithmComparator.domain.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * The type Document repository.
 */
@Repository
public class DocumentRepository extends AbstractRepository<Document> {

    private static final Logger log = LoggerFactory.getLogger(DocumentRepository.class);

    /**
     * Find document by url.
     *
     * @param url the url
     * @return the document
     */
    public Document findByUrl(String url) {
        final Statement statement = select().all()
                .from(getCassandraTableName())
                .allowFiltering()
                .where(eq("url", url));

        statement.setFetchSize(1);
        try {
            return mapper.map(session.execute(statement)).one();
        } catch (Exception e) {
            log.debug("Unsuccessful getting data from Documents.", e);
            return null;
        }
    }

    @Override
    protected Class<Document> getEntityClass() {
        return Document.class;
    }

}
