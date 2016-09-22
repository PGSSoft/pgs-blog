package com.pgs.spark.bigdata.repository;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import com.pgs.spark.bigdata.domain.CassandraEntity;
import org.springframework.beans.factory.annotation.Autowired;
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
public abstract class AbstractRepository<E extends CassandraEntity> {

    /**
     * The Cassandra session.
     */
    @Autowired
    protected Session session;

    /**
     * The Cassandra mapper.
     */
    Mapper<E> mapper;

    private String tableName;

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        mapper = new MappingManager(session).mapper(getEntityClass());
    }

    /**
     * Gets entity class.
     *
     * @return the entity class
     */
    protected abstract Class<E> getEntityClass();

    /**
     * Gets cassandra table name.
     *
     * @return the cassandra table name
     */
    protected String getCassandraTableName() {
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
     * Find all results of given type.
     *
     * @return the list of entities of given type
     */
    public List<E> findAll() {
        Select select = select().all()
                .from(getCassandraTableName());

        return mapper.map(session.execute(select)).all();
    }

    /**
     * Find one result.
     *
     * @param ids the identifiers
     * @return the result
     */
    public E findOne(UUID... ids) {
        return mapper.get(ids);
    }

    /**
     * Save entities.
     *
     * @param entities the entities
     */
    public void save(Iterable<E> entities) {
        entities.forEach(this::save);
    }

    /**
     * Save one entity.
     *
     * @param entity the entity
     * @return the persisted entity
     */
    public E save(E entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        mapper.save(entity);
        return entity;
    }

    /**
     * Delete one entity by id.
     *
     * @param id the id
     */
    public void delete(UUID id) {
        mapper.delete(id);
    }

    /**
     * Delete one entity.
     *
     * @param entity the entity
     */
    public void delete(E entity) {
        mapper.delete(entity);
    }

    /**
     * Delete all entities.
     */
    public void deleteAll() {
        session.execute(truncate(getCassandraTableName()));
    }

}
