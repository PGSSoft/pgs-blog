package com.pgs.spark.bigdata.crawler;

import com.pgs.spark.bigdata.parser.BiznesParserImpl;
import com.pgs.spark.bigdata.parser.EGospodarkaParserImpl;
import com.pgs.spark.bigdata.parser.WebPageParser;
import com.pgs.spark.bigdata.parser.WpParserImpl;
import com.pgs.spark.bigdata.service.DocumentService;
import com.pgs.spark.bigdata.service.SparkCrawlService;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.TextParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BasicCrawlerTest {

    @Mock
    private SparkCrawlService myController;

    @InjectMocks
    private BasicCrawler basicCrawler;

    @Mock
    private DocumentService documentService;

    @Before
    public void setUp() {
        final Map<String, WebPageParser> pagesMap = Stream.of(new WpParserImpl(), new BiznesParserImpl(), new EGospodarkaParserImpl()).collect(Collectors.toMap(WebPageParser::getParsablePage, parser -> parser));
        Mockito.when(myController.getParsersMap()).thenReturn(pagesMap);
        Mockito.when(myController.getDocumentService()).thenReturn(documentService);
    }

    @Test
    public void testShouldVisit() throws Exception {
        WebURL webURL = new WebURL();
        webURL.setURL("www.wp.pl");
        assertTrue(basicCrawler.shouldVisit(null, webURL));

        webURL = new WebURL();
        webURL.setURL("www.wp.pl/image.jpg");
        assertFalse(basicCrawler.shouldVisit(null, webURL));

        webURL = new WebURL();
        webURL.setURL("www.onet.pl");
        assertFalse(basicCrawler.shouldVisit(null, webURL));


    }

    @Test
    public void testVisit() throws Exception {
        final WebURL webURL = new WebURL();
        webURL.setURL("www.bankier.pl");
        final Page page = new Page(webURL);

        ParseData parseData = new TextParseData();
        page.setParseData(parseData);
        basicCrawler.visit(page);
        verify(myController, times(0)).getDocumentService();

        parseData = new HtmlParseData();
        ((HtmlParseData) parseData).setText(RandomStringUtils.random(5));
        ((HtmlParseData) parseData).setHtml(RandomStringUtils.random(5));
        page.setParseData(parseData);
        basicCrawler.visit(page);
        verify(myController, times(0)).getDocumentService();
        verify(myController, times(1)).getParsersMap();

    }

    @Test
    public void testVisitArticle() throws Exception {
        final WebURL webURL = new WebURL();
        webURL.setURL("http://historia.wp.pl/title,Zofia-Szwal-Bog-ocalil-mnie-z-rzezi-Polakow-abym-opowiedziala-te-historie,wid,18413141,wiadomosc.html?ticaid=11758a");
        final Page page = new Page(webURL);

        ParseData parseData = new TextParseData();
        page.setParseData(parseData);
        basicCrawler.visit(page);
        verify(myController, times(0)).getDocumentService();

        parseData = new HtmlParseData();
        ((HtmlParseData) parseData).setText(RandomStringUtils.random(5));
        ((HtmlParseData) parseData).setHtml(RandomStringUtils.random(5));
        page.setParseData(parseData);
        basicCrawler.visit(page);
        verify(myController, times(0)).getDocumentService();

    }


}
