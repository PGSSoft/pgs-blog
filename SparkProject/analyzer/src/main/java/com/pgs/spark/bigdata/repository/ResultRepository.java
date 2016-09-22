package com.pgs.spark.bigdata.repository;

import com.datastax.driver.core.querybuilder.Select.Where;
import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.column;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

@Repository
public class ResultRepository extends AbstractRepository<Result> {

    public List<Result> findBySearchProfile(UUID searchProfileId) {
        return getList(select().all()
            .from(getCassandraTableName()).allowFiltering()
            .where(eq("search_profile_id", searchProfileId)));
    }

    public List<Object[]> findResultsBySearchProfileAndClassificationInRange(final UUID searchProfileId,
                                                                             final Classification type,
                                                                             final LocalDate from,
                                                                             final LocalDate until) {
        final Where statement = select().fcall("group_and_count", column("document_date"))
            .from(getCassandraTableName()).allowFiltering()
            .where(eq("search_profile_id", searchProfileId))
            .and(eq("classification", type.name()))
            .and(gte("document_date", from))
            .and(lte("document_date", until));
        final Map<com.datastax.driver.core.LocalDate, Integer> map = (Map<com.datastax.driver.core.LocalDate, Integer>) session.execute(statement).one().getObject(0);
        return map.entrySet().stream().map(this::toChartObject).collect(Collectors.toList());
    }


    @Override
    protected Class<Result> getEntityClass() {
        return Result.class;
    }

    public Object[] toChartObject(final Entry<com.datastax.driver.core.LocalDate, Integer> result) {
        final long daysSinceEpoch = result.getKey().getDaysSinceEpoch();
        final LocalDate date = LocalDate.ofEpochDay(daysSinceEpoch);
        final long amount = result.getValue().longValue();
        return new Object[]{date, amount};
    }

}
