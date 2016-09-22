package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.domain.Result;
import org.springframework.stereotype.Repository;

/**
 * The type Result repository.
 */
@Repository
public class ResultRepository extends AbstractRepository<Result> {

    @Override
    protected Class<Result> getEntityClass() {
        return Result.class;
    }

}
