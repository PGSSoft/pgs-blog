package com.pgs.spark.bigdata.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Pb website parser.
 */
@Component(value = "pbParser")
public class PbParser extends WebPageParser {

    private final String inTheClipboardSentence = "w schowku Tylko dla zalogowanych czytelników zaloguj się lub zarejestrujdodaj do schowka ";

    private final String format = "yyyy-MM-dd hh:mm";

    @Override
    public String getParsablePage() {
        return "pb.pl";
    }

    @Override
    protected String getTitle(Document document) {
        return document.select(".article h1").text();
    }

    @Override
    protected Set<String> getTags(Document document) {
        return Collections.emptySet();
    }

    @Override
    protected String getDate(Document document) {
        return document.select("article span.article_date").text();
    }

    @Override
    protected String getContent(Document document) {
        final List<Element> articleElements = document.getElementsByClass("article");
        return articleElements.stream()
                .filter(element -> !element.text().isEmpty())
                .map(Element::text)
                .map(text -> text.contains(inTheClipboardSentence) ?
                        text.substring(text.indexOf(inTheClipboardSentence) + inTheClipboardSentence.length()) :
                        text)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.joining());
    }

    @Override
    protected String getDateFormat() {
        return format;
    }
}
