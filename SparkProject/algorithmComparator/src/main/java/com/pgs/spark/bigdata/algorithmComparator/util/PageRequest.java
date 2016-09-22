package com.pgs.spark.bigdata.algorithmComparator.util;

import java.util.UUID;

/**
 * The type Page request.
 */
public class PageRequest {

    private final UUID offsetId;
    private final int limit;

    /**
     * Instantiates a new Page request.
     *
     * @param offsetId the offset id
     * @param limit    the limit
     */
    public PageRequest(UUID offsetId, int limit) {
        this.offsetId = offsetId;
        this.limit = limit;
    }

    /**
     * Has offset boolean.
     *
     * @return the boolean
     */
    public boolean hasOffset() {
        return offsetId != null;
    }

    /**
     * Gets offset id.
     *
     * @return the offset id
     */
    public String getOffsetId() {
        return offsetId.toString();
    }

    /**
     * Gets limit.
     *
     * @return the limit
     */
    public int getLimit() {
        return limit;
    }
}

