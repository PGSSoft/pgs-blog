package com.pgs.spark.bigdata.config;

import com.pgs.spark.bigdata.service.SparkCrawlService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

/**
 * The type Crawler configuration.
 */
@Configuration
@PropertySource("classpath:crawler.properties")
public class CrawlerConfiguration {

    @Value("${crawler.storageFolder}")
    private String storageFolder;

    @Value("${crawler.politenessDelay}")
    private int politnessDelay;

    @Value("${crawler.maxDepthOfCrawling}")
    private int maxDepthOfCrawling;

    @Value("${crawler.maxPagesToFetch}")
    private int maxPagesToFetch;

    @Value("${crawler.connectionTimeout}")
    private int connectionTimeout;

    @Value("${crawler.socketTimeout}")
    private int socketTimeout;

    @Value("${crawler.includeBinaryContentInCrawling}")
    private boolean includeBinaryContentInCrawling;

    @Value("${crawler.resumableCrawling}")
    private boolean resumableCrawling;

    @Value("#{'${crawler.urlSeeds}'.split(',')}")
    private List<String> urlSeeds;

    /**
     * Gets crawler configuration.
     *
     * @return the crawler configuration
     */
    @Bean
    public CrawlConfig getCrawlerConfiguration() {
        final CrawlConfig config = new CrawlConfig();

        config.setCrawlStorageFolder(storageFolder);
        config.setPolitenessDelay(politnessDelay);
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        config.setMaxPagesToFetch(maxPagesToFetch);
        config.setConnectionTimeout(connectionTimeout);
        config.setIncludeBinaryContentInCrawling(includeBinaryContentInCrawling);
        config.setResumableCrawling(resumableCrawling);
        config.setSocketTimeout(socketTimeout);
        config.setUserAgentString("");

        return config;
    }

    /**
     * Crawler4j page fetcher.
     *
     * @param config the config
     * @return the page fetcher
     */
    @Bean
    @Autowired
    public PageFetcher pageFetcher(CrawlConfig config) {
        return new PageFetcher(config);
    }

    /**
     * Robotstxt config.
     *
     * @return the robotstxt config
     */
    @Bean
    public RobotstxtConfig robotstxtConfig() {
        return new RobotstxtConfig();
    }

    /**
     * Robotstxt server config.
     *
     * @param robotstxtConfig the robotstxt config
     * @param pageFetcher     the page fetcher
     * @return the robotstxt server
     */
    @Bean
    @Autowired
    public RobotstxtServer robotstxtServer(RobotstxtConfig robotstxtConfig, PageFetcher pageFetcher) {
        return new RobotstxtServer(robotstxtConfig, pageFetcher);
    }

    /**
     * Crawl controller config.
     *
     * @param config      the config
     * @param pageFetcher the page fetcher
     * @param server      the server
     * @return the spark crawl controller
     * @throws Exception the exception
     */
    @Bean
    @Autowired
    public SparkCrawlService crawlController(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer server) throws Exception {
        final SparkCrawlService controller = new SparkCrawlService(config, pageFetcher, server);
        urlSeeds.forEach(controller::addSeed);
        return controller;
    }

    /**
     * Gets url seeds.
     *
     * @return the url seeds
     */
    public List<String> getUrlSeeds() {
        return urlSeeds;
    }

}
