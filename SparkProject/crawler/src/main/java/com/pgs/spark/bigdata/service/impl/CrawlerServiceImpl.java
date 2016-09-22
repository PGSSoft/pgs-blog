package com.pgs.spark.bigdata.service.impl;

import com.pgs.spark.bigdata.crawler.BasicCrawler;
import com.pgs.spark.bigdata.service.CrawlerService;
import com.pgs.spark.bigdata.service.SparkCrawlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * The type Crawler service.
 */
@Service
@PropertySource("classpath:crawler.properties")
public class CrawlerServiceImpl implements CrawlerService {

    @Value("${crawler.numberOfCrawlers}")
    private int numberOfCrawlers;

    @Autowired
    private SparkCrawlService controller;

    @Override
    public void crawl() {
        controller.start(BasicCrawler.class, numberOfCrawlers);
    }

}
