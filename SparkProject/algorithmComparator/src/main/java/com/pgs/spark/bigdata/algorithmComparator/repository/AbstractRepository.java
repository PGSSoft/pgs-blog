package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import com.pgs.spark.bigdata.algorithmComparator.domain.CassandraEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.truncate;

/**
 * The type Abstract repository.
 *
 * @param <E> the type parameter
 */
@Repository
@PropertySource("classpath:application.properties")
public abstract class AbstractRepository<E extends CassandraEntity> {

    @Value("${spring.data.cassandra.keyspaceName}")
    private String keySpaceName;

    /**
     * The Session bean.
     */
    @Autowired
    protected Session session;

    /**
     * The Mapper.
     */
    Mapper<E> mapper;

    private String tableName;

    /**
     * Init method.
     */
    @PostConstruct
    public void init() {
        mapper = new MappingManager(session).mapper(getEntityClass());
    }

    /**
     * Gets domain entity class of these repository.
     *
     * @return the entity class
     */
    protected abstract Class<E> getEntityClass();

    /**
     * Gets key space name for given domain entity.
     *
     * @return the key space name
     */
    public String getKeySpaceName() {
        return keySpaceName;
    }

    /**
     * Gets cassandra table name.
     *
     * @return the cassandra table name
     */
    String getCassandraTableName() {
        if (tableName == null) {
            final Table table = getEntityClass().getAnnotation(Table.class);
            if (table == null) {
                throw new RuntimeException(String.format(
                        "The class %s is not a valid cassandra entity. It lacks the com.datastax.driver.mapping.annotations.Table annotation.",
                        getEntityClass().getCanonicalName()));
            }
            tableName = table.name();
        }

        return tableName;
    }

    /**
     * Gets list from query.
     *
     * @param statement the statement
     * @return the list
     */
    List<E> getList(Statement statement) {
        return mapper.map(session.execute(statement)).all();
    }

    /**
     * Count number of results.
     *
     * @param statement the statement
     * @return the count
     */
    Long getCount(Statement statement) {
        return session.executeAsync(statement).getUninterruptibly().one().getLong(0);
    }

    /**
     * Gets single result.
     *
     * @param statement the statement
     * @return the single result
     */
    E getSingleResult(Statement statement) {
        return mapper.map(session.execute(statement)).one();
    }

    /**
     * Find all results.
     *
     * @return the list
     */
    public List<E> findAll() {
        return getList(select().all()
                .from(getCassandraTableName()));
    }

    /**
     * Find one result.
     *
     * @param ids the ids
     * @return the result
     */
    public E findOne(UUID... ids) {
        return mapper.get(ids);
    }

    /**
     * Save entity.
     *
     * @param entity the entity
     * @return persisted entity
     */
    public E save(E entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        mapper.save(entity);
        return entity;
    }

    /**
     * Delete single entity.
     *
     * @param entity to delete
     */
    public void delete(E entity) {
        mapper.delete(entity);
    }

    /**
     * Delete single entity by id.
     *
     * @param id of entity to delete
     */
    public void delete(UUID id) {
        mapper.delete(id);
    }

    /**
     * Delete all entities.
     */
    public void deleteAll() {
        session.execute(truncate(getCassandraTableName()));
    }

}
