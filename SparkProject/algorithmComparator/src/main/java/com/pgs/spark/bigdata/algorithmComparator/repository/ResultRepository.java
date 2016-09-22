package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Select.Where;
import com.pgs.spark.bigdata.algorithmComparator.domain.Result;
import com.pgs.spark.bigdata.algorithmComparator.util.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.fcall;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.token;

/**
 * The type Result repository.
 */
@Repository
public class ResultRepository extends AbstractRepository<Result> {

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
     * Find results by search profile to training.
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
     * Find results to estimating by search profile.
     *
     * @param searchProfileId the search profile id
     * @param pageRequest     the page request
     * @return the list
     */
    public List<Result> findBySearchProfileToEstimating(UUID searchProfileId, PageRequest pageRequest) {
        Where statement = select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId));

        if (pageRequest.hasOffset()) {
            statement.and(gt(token("id"), fcall("token", UUID.fromString(pageRequest.getOffsetId()))));
        }

        statement.limit(pageRequest.getLimit());

        return getList(statement);
    }

    /**
     * Count number of results.
     *
     * @return the long
     */
    public Long count() {
        return getCount(select().countAll().from(getCassandraTableName()));
    }

    /**
     * Gets one result by id.
     *
     * @param resultId the result id
     * @return result
     */
    public Result getOneByResultId(final UUID resultId) {
        final Statement statement = select().from(getCassandraTableName()).allowFiltering().where(eq("id", resultId));
        return getSingleResult(statement);
    }

    @Override
    protected Class<Result> getEntityClass() {
        return Result.class;
    }
}

