package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.pgs.spark.bigdata.algorithmComparator.domain.Algorithm;
import com.pgs.spark.bigdata.algorithmComparator.domain.AlgorithmAccuracy;
import com.pgs.spark.bigdata.algorithmComparator.domain.ClassificationGroup;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.remove;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;

/**
 * The type Algorithm accuracy repository.
 */
@Repository
public class AlgorithmAccuracyRepository extends AbstractRepository<AlgorithmAccuracy> {

    /**
     * Create or update by algorithm optional.
     *
     * @param algorithm the algorithm
     * @param accuracy  the accuracy
     */
    public void createOrUpdateByAlgorithm(final Algorithm algorithm, final int accuracy, final UUID searchProfileId) {
        AlgorithmAccuracy algorithmAccuracy = getByAlgorithm(algorithm, searchProfileId).orElseGet(() ->
                AlgorithmAccuracy.builder()
                .name(algorithm.name())
                .searchProfileId(searchProfileId)
                .accuracy(accuracy).build());
        algorithmAccuracy.setAccuracy(accuracy);
        saveOrUpdate(algorithmAccuracy);
    }

    /**
     * Gets algorithm accuracy by algorithm.
     *
     * @param algorithm the algorithm
     * @return the by algorithm
     */
    public Optional<AlgorithmAccuracy> getByAlgorithm(final Algorithm algorithm, final UUID searchProfileId) {
        final Statement statement = select().all().from(getCassandraTableName()).allowFiltering()
                .where(eq("name", algorithm.name()))
                .and(eq("search_profile_id", searchProfileId));

        return Optional.ofNullable(getSingleResult(statement));
    }

    private void saveOrUpdate(AlgorithmAccuracy algorithmAccuracy) {
        if (algorithmAccuracy.getId() == null) {
            save(algorithmAccuracy);
        } else {
            update(algorithmAccuracy);
        }
    }

    public void update(AlgorithmAccuracy algorithmAccuracy) {
        final Statement statement = QueryBuilder.update(getCassandraTableName())
                .with(set("accuracy", algorithmAccuracy.getAccuracy()))
                .where(eq("id", algorithmAccuracy.getId()));
        session.execute(statement);
    }

    @Override
    protected Class<AlgorithmAccuracy> getEntityClass() {
        return AlgorithmAccuracy.class;
    }

    public void deleteBySearchProfileId(UUID searchProfileId) {
        List<AlgorithmAccuracy> algorithmAccuracies = getList(select().all()
                .from(getCassandraTableName()).allowFiltering()
                .where(eq("search_profile_id", searchProfileId)));
        algorithmAccuracies.forEach(algorithmAccuracy -> mapper.delete(algorithmAccuracy.getId()));
    }
}
