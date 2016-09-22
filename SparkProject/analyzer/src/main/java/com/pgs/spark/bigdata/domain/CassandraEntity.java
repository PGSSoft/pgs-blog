package com.pgs.spark.bigdata.domain;

import java.io.Serializable;
import java.util.UUID;

public abstract class CassandraEntity implements Serializable {

    private static final long serialVersionUID = 6260666531530802991L;

    public abstract UUID getId();

    public abstract void setId(UUID uuid);
}
