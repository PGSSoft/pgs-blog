package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.domain.SearchProfile;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * Spring Data JPA repository for the SearchProfile entity.
 */
@Repository
public class SearchProfileRepository extends AbstractRepository<SearchProfile> {

    @Override
    protected Class<SearchProfile> getEntityClass() {
        return SearchProfile.class;
    }

}
