package com.pgs.spark.bigdata.processor.service;

import org.apache.spark.sql.DataFrame;

import java.util.UUID;

/**
 * The interface Tag classification service.
 */
public interface TagClassificationService {

    /**
     * Gets by super tags.
     *
     * @param searchProfileId the search profile id
     * @return the by super tags
     */
    DataFrame getBySuperTags(UUID searchProfileId);

    /**
     * Gets without super tag.
     *
     * @param searchProfileId the search profile id
     * @return the without super tag
     */
    DataFrame getWithoutSuperTag(UUID searchProfileId);
}
