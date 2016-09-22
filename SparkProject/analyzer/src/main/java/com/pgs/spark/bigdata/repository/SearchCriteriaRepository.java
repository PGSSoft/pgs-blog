package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.domain.SearchCriteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

@Repository
public class SearchCriteriaRepository extends AbstractRepository<SearchCriteria> {

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
