package com.pgs.spark.bigdata.processor.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * The type Cassandra entity.
 */
public abstract class CassandraEntity implements Serializable {

    private static final long serialVersionUID = 6260666531530802991L;

    /**
     * Gets id.
     *
     * @return the id
     */
    public abstract UUID getId();

    /**
     * Sets id.
     *
     * @param uuid the uuid
     */
    public abstract void setId(UUID uuid);
}
