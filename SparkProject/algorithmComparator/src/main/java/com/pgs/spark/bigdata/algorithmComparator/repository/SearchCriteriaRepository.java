package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Statement;
import com.pgs.spark.bigdata.algorithmComparator.domain.SearchCriteria;
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

    @Override
    protected Class<SearchCriteria> getEntityClass() {
        return SearchCriteria.class;
    }

    /**
     * Find search criteria by search profile id.
     *
     * @param searchProfileId the search profile id
     * @return the list of search criteria
     */
    public List<SearchCriteria> findAllBySearchProfileId(UUID searchProfileId) {
        final Statement statement = select().all()
                .from(getCassandraTableName())
                .allowFiltering()
                .where(eq("search_profile_id", searchProfileId));

        return mapper.map(session.execute(statement)).all();
    }
}
