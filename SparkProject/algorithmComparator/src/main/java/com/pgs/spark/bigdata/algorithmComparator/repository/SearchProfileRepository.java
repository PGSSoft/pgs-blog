package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.pgs.spark.bigdata.algorithmComparator.domain.SearchProfile;
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
