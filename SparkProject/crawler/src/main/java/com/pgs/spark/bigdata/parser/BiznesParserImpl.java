package com.pgs.spark.bigdata.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * The Biznes website parser.
 */
@Component(value = "biznesParser")
public class BiznesParserImpl extends WebPageParser {

    private final String format = "yyyy-MM-dd hh:mm:ssZ";

    @Override
    public String getParsablePage() {
        return "biznes.pl";
    }

    @Override
    protected String getTitle(Document document) {
        return document.select("#articleHeading #mainTitle").text().trim();
    }

    @Override
    protected Set<String> getTags(Document document) {
        return document.select("#articleDetail #main #relatedTopics").stream()
                .flatMap(element -> asList(element.text().replaceAll("(^(\\S|\\s)\\w+\\s:)|(\\S\\w+:\\s)|,|\\.\\.\\.", "").split(" ")).stream())
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    protected String getDate(final Document document) {
        return document.select("#articleHeading meta").attr("content");
    }

    @Override
    protected String getContent(final Document document) {
        final Elements elements = document.select("#articleDetail #main");
        return elements.stream().map(Element::text).collect(Collectors.joining());
    }

    @Override
    protected String getDateFormat() {
        return format;
    }
}
