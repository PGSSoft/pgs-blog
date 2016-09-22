package com.pgs.spark.bigdata.processor.repository;

import com.pgs.spark.bigdata.processor.domain.SearchProfile;
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
