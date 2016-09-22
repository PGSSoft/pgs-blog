package com.pgs.spark.bigdata.service.socialMediaCrawling;

import com.pgs.spark.bigdata.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * The type Social media crawling service.
 */
public abstract class SocialMediaCrawlingService {

    /**
     * The Document service.
     */
    @Autowired
    protected DocumentService documentService;

    /**
     * Crawl.
     *
     * @param keywords the keywords
     */
    public abstract void crawl(final Set<String> keywords);

}
