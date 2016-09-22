package com.pgs.spark.bigdata.processor.repository;

import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.pgs.spark.bigdata.processor.domain.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;
import static com.datastax.driver.core.querybuilder.QueryBuilder.set;

/**
 * The type Tag repository.
 */
@Repository
public class TagRepository extends AbstractRepository<Tag> {

    @Override
    protected Class<Tag> getEntityClass() {
        return Tag.class;
    }

    /**
     * Find super tag by child.
     *
     * @param tag the tag
     * @return the optional
     */
    public Optional<Tag> findSuperTagByChild(final Tag tag) {
        final Statement statement = select()
                .from(getCassandraTableName())
                .where(eq("id", tag.getParentId()));
        try {
            return Optional.ofNullable(getList(statement).get(0));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    /**
     * Gets super tags.
     *
     * @return the super tags
     */
    public List<Tag> getSuperTags() {
        final Statement statement = select()
                .from(getCassandraTableName())
                .allowFiltering()
                .where(eq("is_super_tag", true));
        return getList(statement);
    }

    /**
     * Gets child tags.
     *
     * @param superTag the super tag
     * @return the child tags
     */
    public List<Tag> getChildTags(final Tag superTag) {
        final Statement statement = select()
                .from(getCassandraTableName())
                .allowFiltering()
                .where(eq("parent_id", superTag.getId()));

        return getList(statement);
    }

    /**
     * Gets tags without super tag.
     *
     * @return the tags without super tag
     */
    public List<Tag> getWithoutSuperTag() {
        final Statement statement = QueryBuilder.select().all()
                .from(getCassandraTableName())
                .allowFiltering()
                .where(eq("is_assigned_to_supertag", false))
                .and(eq("is_super_tag", false));
        return getList(statement);
    }
}
