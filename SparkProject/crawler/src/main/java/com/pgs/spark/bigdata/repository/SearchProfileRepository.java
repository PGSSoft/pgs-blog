package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.domain.SearchProfile;
import org.springframework.stereotype.Repository;

/**
 * The type Search profile repository.
 */
@Repository
public class SearchProfileRepository extends AbstractRepository<SearchProfile> {

    @Override
    protected Class<SearchProfile> getEntityClass() {
        return SearchProfile.class;
    }
}
