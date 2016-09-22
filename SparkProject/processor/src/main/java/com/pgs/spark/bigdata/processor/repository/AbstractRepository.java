package com.pgs.spark.bigdata.processor.repository;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import com.pgs.spark.bigdata.processor.domain.CassandraEntity;
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
     * The Cassandra session.
     */
    @Autowired
    protected Session session;

    /**
     * The Cassandra mapper.
     */
    protected Mapper<E> mapper;

    private String tableName;

    /**
     * Init method.
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
     * Gets key space name.
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
     * Gets list of given type.
     *
     * @param statement the statement
     * @return the list
     */
    protected List<E> getList(Statement statement) {
        return mapper.map(session.execute(statement)).all();
    }

    /**
     * Find one entity.
     *
     * @param ids the ids
     * @return the e
     */
    public E findOne(UUID... ids) {
        return mapper.get(ids);
    }

    /**
     * Save entity.
     *
     * @param entity the entity
     * @return the e
     */
    public synchronized E save(E entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        mapper.save(entity);
        return entity;
    }

    /**
     * Delete entity.
     *
     * @param id the id of entity
     */
    public void delete(UUID id) {
        mapper.delete(id);
    }

    /**
     * Delete entity.
     *
     * @param entity the entity
     */
    public void delete(E entity) {
        mapper.delete(entity);
    }

}
