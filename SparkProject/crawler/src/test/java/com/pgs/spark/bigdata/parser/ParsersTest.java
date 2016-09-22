package com.pgs.spark.bigdata.parser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.dto.ArticleDTO;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ParsersTest {

    private final List<WebPageParser> parses = Arrays.asList(new BankierParserImpl(), new BiznesParserImpl(), new EGospodarkaParserImpl(), new PbParser(), new WpParserImpl());

    private final Map<String, WebPageParser> parsers = parses.stream().collect(Collectors.toMap(WebPageParser::getParsablePage, parser -> parser));

    @Test
    public void shouldParseExampleBankierDocument() throws IOException, ParseException {
        //given
        final HtmlParseData htmlParseData = new HtmlParseData();

        final String html = TestUtil.readFileAsString("src/test/resources/bankierExamplePage.html");
        htmlParseData.setHtml(html);

        //when
        final ArticleDTO articleTO = parsers.get("bankier.pl").parse(htmlParseData).get();

        //then
        assertEquals("Tesla Motors pobiła Volkswagena", articleTO.getTitle());
        assertEquals(LocalDate.of(2016, 6, 9), articleTO.getDate());
        assertEquals(Sets.newHashSet("Tesla", "Volkswagen"), articleTO.getTags().stream().map(Tag::getContent).collect(Collectors.toSet()));
        assertTrue(articleTO.getTags().size() > 0);
    }

    @Test
    public void shouldParseExampleBiznesDocument() throws IOException, ParseException {
        //given
        final HtmlParseData htmlParseData = new HtmlParseData();

        final String html = TestUtil.readFileAsString("src/test/resources/biznesExamplePage.html");
        htmlParseData.setHtml(html);

        //when
        final ArticleDTO articleTO = parsers.get("biznes.pl").parse(htmlParseData).get();

        //then
        assertEquals("Wchodzi w życie nowelizacja ustawy o VAT", articleTO.getTitle());
        assertEquals(LocalDate.of(2016, 8, 1), articleTO.getDate());
        assertTrue(articleTO.getTags().size() > 0);
    }

    @Test
    public void shouldParseExampleEgospodarkaDocument() throws IOException, ParseException {
        //given
        final HtmlParseData htmlParseData = new HtmlParseData();

        final String html = TestUtil.readFileAsString("src/test/resources/egospodarkaExamplePage.html");
        htmlParseData.setHtml(html);

        //when
        final ArticleDTO articleTO = parsers.get("egospodarka.pl").parse(htmlParseData).get();

        //then
        assertEquals("CEIDG - jakie zmiany? Przy podawaniu adresu trzeba mieć do niego prawo Aktualizacja i odpowiedzialność za jej brak Wykreślanie i korekta bez postępowania", articleTO.getTitle());
        assertEquals(LocalDate.of(2016, 6, 13), articleTO.getDate());
        assertTrue(articleTO.getTags().size() == 0);
    }

    @Test
    public void shouldParseExamplePbDocument() throws IOException, ParseException {
        //given
        final HtmlParseData htmlParseData = new HtmlParseData();

        final String html = TestUtil.readFileAsString("src/test/resources/pbExamplePage.html");
        htmlParseData.setHtml(html);

        //when
        final ArticleDTO articleTO = parsers.get("pb.pl").parse(htmlParseData).get();

        //then
        assertEquals("Mamy gorący rynek pracy", articleTO.getTitle());
        assertEquals(LocalDate.now(), articleTO.getDate());
        assertTrue(articleTO.getTags().size() == 0);
    }

    @Test
    public void shouldParseExampleWpDocument() throws IOException, ParseException {
        //given
        final HtmlParseData htmlParseData = new HtmlParseData();

        final String html = TestUtil.readFileAsString("src/test/resources/wpExamplePage.html");
        htmlParseData.setHtml(html);

        //when
        final ArticleDTO articleTO = parsers.get("wp.pl").parse(htmlParseData).get();

        //then
        assertEquals("Londyńska policja spodziewa się kolejnego zamachu? \"Kwestia kiedy, a nie czy nastąpi\"", articleTO.getTitle());
        assertEquals(LocalDate.of(2016, 8, 1), articleTO.getDate());
        assertTrue(articleTO.getTags().size() > 0);
    }

}