package com.pgs.spark.bigdata.processor.repository;

import com.pgs.spark.bigdata.processor.domain.SearchCriteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * The type Search criteria repository.
 */
@Repository
public class SearchCriteriaRepository extends AbstractRepository<SearchCriteria> {

    /**
     * Find SearchCriteria by search profile id.
     *
     * @param searchProfileId the search profile id
     * @return the list
     */
    public List<SearchCriteria> findBySearchProfileId(UUID searchProfileId) {
        return getList(select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId)));
    }

    @Override
    protected Class<SearchCriteria> getEntityClass() {
        return SearchCriteria.class;
    }

}
