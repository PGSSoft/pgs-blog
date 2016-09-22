package com.pgs.spark.bigdata.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * The E-gospodarka website parser.
 */
@Component(value = "eGospodarkaParser")
public class EGospodarkaParserImpl extends WebPageParser {

    private final String format = "yyyy-MM-dd";

    @Override
    public String getParsablePage() {
        return "egospodarka.pl";
    }

    @Override
    protected String getTitle(Document document) {
        return document.select("div.ego-art-line h1,h2,h3,h4,h5,h6").text();
    }

    @Override
    protected Set<String> getTags(Document document) {
        return document.select("div.ego-scr .art-wiecej-temat")
                .stream()
                .flatMap(element -> asList(element.text().replaceAll("^.+:", "").replaceAll(",", " ").split(" ")).stream())
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    protected String getDate(Document document) {
        return document.select("div.ego-art div.ego-art-line p.art-data a").text();
    }

    @Override
    protected String getContent(Document document) {
        return document.select("div.art-p").stream().map(Element::text).collect(Collectors.joining());
    }

    @Override
    protected String getDateFormat() {
        return format;
    }
}
