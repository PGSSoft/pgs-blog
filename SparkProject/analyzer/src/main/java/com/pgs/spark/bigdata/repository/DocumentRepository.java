package com.pgs.spark.bigdata.repository;

import com.datastax.driver.core.querybuilder.Select;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.repository.util.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.fcall;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.token;

@Repository
public class DocumentRepository extends AbstractRepository<Document> {

    @Override
    protected Class<Document> getEntityClass() {
        return Document.class;
    }

    public synchronized List<Document> findAll(PageRequest pageRequest) {
        final Select statement = select().all()
            .from(getCassandraTableName()).allowFiltering();

        if (pageRequest.hasOffset()) {
            statement.where(gt(token("id"), fcall("token", UUID.fromString(pageRequest.getOffsetId()))));
        }

        statement.limit(pageRequest.getLimit());

        return getList(statement);
    }

}
