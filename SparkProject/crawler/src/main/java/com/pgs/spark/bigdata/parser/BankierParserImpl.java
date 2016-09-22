package com.pgs.spark.bigdata.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * The Bankier website parser.
 */
@Component(value = "bankierParser")
public class BankierParserImpl extends WebPageParser {

    private final String format = "yyyy-MM-dd hh:mm:ss";

    @Override
    public String getParsablePage() {
        return "bankier.pl";
    }

    @Override
    protected String getTitle(Document document) {
        return document.select("article header .entry-title").text();
    }

    @Override
    protected Set<String> getTags(Document document) {
        return document.select("article footer #articleTag .boxContent")
                .stream()
                .flatMap(element -> asList(element.text().replaceAll("(^(\\S|\\s)\\w+\\s:)|(\\S\\w+:\\s)", "").split(" ")).stream())
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    protected String getDate(Document document) {
        return document.select("article header time").attr("datetime");
    }

    @Override
    protected String getContent(Document document) {
        final List<Element> articleElements = document.select("article #articleContent");
        return articleElements.stream().map(Element::text).collect(Collectors.joining());
    }

    @Override
    protected String getDateFormat() {
        return format;
    }

}
