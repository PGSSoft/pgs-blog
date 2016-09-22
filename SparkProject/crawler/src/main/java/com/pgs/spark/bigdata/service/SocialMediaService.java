package com.pgs.spark.bigdata.service;

import java.util.List;
import java.util.UUID;

/**
 * The interface Social media service.
 */
public interface SocialMediaService {
    /**
     * List social medias.
     *
     * @return the list
     */
    List<String> listSocialMedias();

    /**
     * Perform crawling on given website.
     *
     * @param website         the website
     * @param searchProfileId the search profile id
     */
    void performCrawling(final String website, final UUID searchProfileId);
}
