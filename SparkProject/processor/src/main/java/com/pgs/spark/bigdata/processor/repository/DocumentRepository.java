package com.pgs.spark.bigdata.processor.repository;

import com.pgs.spark.bigdata.processor.domain.Document;
import org.springframework.stereotype.Repository;

/**
 * The type Document repository.
 */
@Repository
public class DocumentRepository extends AbstractRepository<Document> {

    @Override
    protected Class<Document> getEntityClass() {
        return Document.class;
    }

}
