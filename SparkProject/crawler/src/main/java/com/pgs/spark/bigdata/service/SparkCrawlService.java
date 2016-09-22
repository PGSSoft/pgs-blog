package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.parser.WebPageParser;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Spark crawl controller.
 */
public class SparkCrawlService extends CrawlController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private List<WebPageParser> parsers;

    /**
     * Instantiates a new Spark crawl controller.
     *
     * @param config          the config
     * @param pageFetcher     the page fetcher
     * @param robotstxtServer the robotstxt server
     * @throws Exception the exception
     */
    public SparkCrawlService(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
        super(config, pageFetcher, robotstxtServer);
    }

    /**
     * Gets document service.
     *
     * @return the document service
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Gets parsers map.
     *
     * @return the parsers map
     */
    public Map<String, WebPageParser> getParsersMap() {
        return parsers.stream().collect(Collectors.toMap(WebPageParser::getParsablePage, Function.identity()));
    }

}
