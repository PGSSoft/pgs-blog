package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.domain.Tag;
import org.springframework.stereotype.Repository;

/**
 * The type Tag repository.
 */
@Repository
public class TagRepository extends AbstractRepository<Tag> {

    @Override
    protected Class<Tag> getEntityClass() {
        return Tag.class;
    }
}