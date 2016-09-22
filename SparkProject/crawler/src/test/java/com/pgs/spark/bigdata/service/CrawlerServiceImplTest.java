package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.crawler.BasicCrawler;
import com.pgs.spark.bigdata.service.impl.CrawlerServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

public class CrawlerServiceImplTest {

    @InjectMocks
    private CrawlerServiceImpl crawlerService = new CrawlerServiceImpl();

    @Mock
    private SparkCrawlService sparkCrawlController;

    private final int numberOfCrawlers = 100;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(crawlerService, "numberOfCrawlers", numberOfCrawlers);
    }

    @Test
    public void shouldStartCrawling() {
        //when
        crawlerService.crawl();

        //then
        verify(sparkCrawlController).start(BasicCrawler.class, numberOfCrawlers);
    }

}



