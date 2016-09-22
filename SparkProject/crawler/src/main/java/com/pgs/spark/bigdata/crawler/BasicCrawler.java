package com.pgs.spark.bigdata.crawler;

import com.pgs.spark.bigdata.parser.WebPageParser;
import com.pgs.spark.bigdata.service.DocumentService;
import com.pgs.spark.bigdata.service.SparkCrawlService;
import com.pgs.spark.bigdata.dto.ArticleDTO;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The type Basic crawler.
 */
public class BasicCrawler extends WebCrawler {

    private static final Pattern RESTRICTED_EXTENSIONS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        final String href = url.getURL().toLowerCase();
        final boolean isSubsite = getParsers().containsKey(url.getDomain());
        final boolean isNotRestrictedExtension = !RESTRICTED_EXTENSIONS.matcher(href).matches();
        return isNotRestrictedExtension && isSubsite;
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            final HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            logger.info("VISITED DOMAIN: {}", page.getWebURL().getDomain());
            logger.info("VISITED PAGE: {}", page.getWebURL().getURL());
            logger.info("TEXT LENGTH: {}", htmlParseData.getText().length());
            logger.info("HTML LENGTH: {}", htmlParseData.getHtml().length());
            parseDocumentAndSendToDb(page, htmlParseData);
        }
    }

    private void parseDocumentAndSendToDb(Page page, HtmlParseData htmlParseData) {
        final List<ArticleDTO> articlesList = new ArrayList<>();
        final WebURL webURL = page.getWebURL();
        final String domain = webURL.getDomain();
        if (getParsers().containsKey(domain)) {
            final WebPageParser parser = getParsers().get(domain);
            parser.parse(htmlParseData).ifPresent(articlesList::add);
        }
        articlesList.forEach(articleContent -> getDocumentService().addDocument(webURL.getURL(), articleContent));
    }

    private Map<String, WebPageParser> getParsers() {
        return getMyController().getParsersMap();
    }

    private DocumentService getDocumentService() {
        return getMyController().getDocumentService();
    }

    @Override
    public SparkCrawlService getMyController() {
        return (SparkCrawlService) super.getMyController();
    }
}
