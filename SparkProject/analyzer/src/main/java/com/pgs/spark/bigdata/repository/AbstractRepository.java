package com.pgs.spark.bigdata.repository;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.annotations.Table;
import com.pgs.spark.bigdata.domain.CassandraEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.fcall;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.in;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.token;
import static com.datastax.driver.core.querybuilder.QueryBuilder.truncate;

@Repository
public abstract class AbstractRepository<E extends CassandraEntity> {

    @Autowired
    protected Session session;

    protected Mapper<E> mapper;

    private String tableName;

    @PostConstruct
    public void init() {
        mapper = new MappingManager(session).mapper(getEntityClass());
    }

    protected abstract Class<E> getEntityClass();

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

    protected List<E> getList(Statement statement) {
        return mapper.map(session.execute(statement)).all();
    }

    protected E getSingleResult(Statement statement) {
        return mapper.map(session.execute(statement)).one();
    }

    public List<E> findAll() {
        return getList(select().all()
        .from(getCassandraTableName()));
    }

    public List<E> findAll(Long userId) {
        return getList(select()
            .from(getCassandraTableName())
            .allowFiltering()
            .where(eq("userId", userId)));
    }

    public List<E> findAll(List<UUID> searchProfileIds) {
        return getList(select()
            .from(getCassandraTableName())
            .allowFiltering()
            .where(in("search_profile_id", searchProfileIds)));
    }

    public long count() {
        return session.execute(select().countAll()
            .from(getCassandraTableName())).one().getLong(0);
    }


    public List<E> findByIds(List<UUID> documentsIds) {
        return getList(select().all()
            .from(getCassandraTableName())
            .where(in("id", documentsIds)));
    }

    public Page<E> findAllPageable(Pageable pageable) {
        UUID lastUUID = null;
        if (pageable.getPageNumber() > 0) {
            final List<Row> all = session.execute(select().column("id").from(getCassandraTableName()).limit(pageable.getPageNumber() * pageable.getPageSize())).all();
            lastUUID = all.get(all.size() - 1).getUUID("id");
        }
        Statement statement = select().all()
            .from(getCassandraTableName())
            .limit(pageable.getPageSize());
        if (lastUUID != null) {
            statement = ((Select) statement).where(gt(token("id"), fcall("token", lastUUID)));
        }
        return new PageImpl<E>(getList(statement), pageable, session.execute(select().countAll().from(getCassandraTableName())).one().getLong(0));
    }

    public Page<E> findAllPageable(Pageable pageable, List<UUID> searchProfileIds) {
        UUID lastUUID = null;
        if (pageable.getPageNumber() > 0) {
            final List<Row> all = session.execute(select()
                .column("id")
                .from(getCassandraTableName())
                .limit(pageable.getPageNumber() * pageable.getPageSize())
                .allowFiltering()
                .where(in("search_profile_id", searchProfileIds))).all();
            lastUUID = all.get(all.size() - 1).getUUID("id");
        }
        Statement statement = select().all()
            .from(getCassandraTableName())
            .limit(pageable.getPageSize())
            .allowFiltering().where(in("search_profile_id", searchProfileIds));
        if (lastUUID != null) {
            statement = ((Select.Where) statement).and(gt(token("id"), fcall("token", lastUUID)));
        }

        return new PageImpl<E>(getList(statement), pageable, session.execute(select().countAll().from(getCassandraTableName()).allowFiltering().where(in("search_profile_id", searchProfileIds))).one().getLong(0));
    }

    public E findOne(UUID... ids) {
        return mapper.get(ids);
    }

    public E save(E entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        mapper.save(entity);
        return entity;
    }

    public void delete(UUID id) {
        mapper.delete(id);
    }

    public void delete(E entity) {
        mapper.delete(entity);
    }

    public void deleteAll() {
        session.execute(truncate(getCassandraTableName()));
    }

}
