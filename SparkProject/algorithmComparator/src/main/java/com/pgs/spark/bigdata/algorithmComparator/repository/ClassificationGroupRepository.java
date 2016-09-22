package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Select;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import com.pgs.spark.bigdata.algorithmComparator.domain.ClassificationGroup;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.column;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lte;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * The type Classification group repository.
 */
@Repository
@SuppressWarnings("unchecked")
public class ClassificationGroupRepository extends AbstractRepository<ClassificationGroup> {

    /**
     * Find classification group by result id.
     *
     * @param resultId the result id
     * @return Optional of classification group
     */
    public Optional<ClassificationGroup> findByResultId(UUID resultId) {
        final Statement statement = select().from(getCassandraTableName()).allowFiltering().where(eq("result_id", resultId));
        return Optional.ofNullable(getSingleResult(statement));
    }


    /**
     * Find results by search profile and simple classification in range of dates list.
     *
     * @param searchProfileId the search profile id
     * @param type            the type
     * @param from            the from
     * @param until           the until
     * @return the list
     */
    public List<Object[]> findResultsBySearchProfileAndSimpleClassificationInRange(final UUID searchProfileId,
                                                                                   Classification type,
                                                                                   LocalDate from,
                                                                                   LocalDate until) {
        final Select.Where statement = select().fcall("group_and_count", column("document_date"))
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId))
                .and(eq("simple_classification", type.name()))
                .and(gte("document_date", from))
                .and(lte("document_date", until));
        final Map<com.datastax.driver.core.LocalDate, Integer> map = (Map<com.datastax.driver.core.LocalDate, Integer>) session.execute(statement).one().getObject(0);
        return map.entrySet().stream().map(this::toChartObject).collect(Collectors.toList());
    }

    /**
     * Find results by search profile and cross classification in range of dates.
     *
     * @param searchProfileId the search profile id
     * @param type            the type
     * @param from            the from
     * @param until           the until
     * @return the list
     */
    public List<Object[]> findResultsBySearchProfileAndCrossClassificationInRange(final UUID searchProfileId,
                                                                                  Classification type,
                                                                                  LocalDate from,
                                                                                  LocalDate until) {
        final Select.Where statement = select().fcall("group_and_count", column("document_date"))
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId))
                .and(eq("cross_classification", type.name()))
                .and(gte("document_date", from))
                .and(lte("document_date", until));
        final Map<com.datastax.driver.core.LocalDate, Integer> map = (Map<com.datastax.driver.core.LocalDate, Integer>) session.execute(statement).one().getObject(0);
        return map.entrySet().stream().map(this::toChartObject).collect(Collectors.toList());
    }

    /**
     * Find results by search profile and multilayer classification in range of dates.
     *
     * @param searchProfileId the search profile id
     * @param type            the type
     * @param from            the from
     * @param until           the until
     * @return the list
     */
    public List<Object[]> findResultsBySearchProfileAndMultilayerClassificationInRange(final UUID searchProfileId,
                                                                                       Classification type,
                                                                                       LocalDate from,
                                                                                       LocalDate until) {
        final Select.Where statement = select().fcall("group_and_count", column("document_date"))
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId))
                .and(eq("multilayer_classification", type.name()))
                .and(gte("document_date", from))
                .and(lte("document_date", until));
        final Map<com.datastax.driver.core.LocalDate, Integer> map = (Map<com.datastax.driver.core.LocalDate, Integer>) session.execute(statement).one().getObject(0);
        return map.entrySet().stream().map(this::toChartObject).collect(Collectors.toList());
    }

    /**
     * Find results by search profile and train classification in range of dates.
     *
     * @param searchProfileId the search profile id
     * @param type            the type
     * @param from            the from
     * @param until           the until
     * @return the list
     */
    public List<Object[]> findResultsBySearchProfileAndTrainClassificationInRange(final UUID searchProfileId,
                                                                                  Classification type,
                                                                                  LocalDate from,
                                                                                  LocalDate until) {
        final Select.Where statement = select().fcall("group_and_count", column("document_date"))
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId))
                .and(eq("train_classification", type.name()))
                .and(gte("document_date", from))
                .and(lte("document_date", until));
        final Map<com.datastax.driver.core.LocalDate, Integer> map = (Map<com.datastax.driver.core.LocalDate, Integer>) session.execute(statement).one().getObject(0);
        return map.entrySet().stream().map(this::toChartObject).collect(Collectors.toList());
    }

    @Override
    protected Class<ClassificationGroup> getEntityClass() {
        return ClassificationGroup.class;
    }

    private Object[] toChartObject(Map.Entry<com.datastax.driver.core.LocalDate, Integer> result) {
        final long daysSinceEpoch = result.getKey().getDaysSinceEpoch();
        final LocalDate date = LocalDate.ofEpochDay(daysSinceEpoch);
        final long amount = result.getValue().longValue();
        return new Object[]{date, amount};
    }

    public void deleteBySearchProfileId(UUID searchProfileId) {
        List<ClassificationGroup> classificationGroups = getList(select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId)));
        classificationGroups.forEach(classificationGroup -> mapper.delete(classificationGroup.getId()));
    }
}
