package com.pgs.spark.bigdata.parser;

import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.dto.ArticleDTO;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The abstract Web page parser.
 */
@Component
public abstract class WebPageParser {

    /**
     * Gets title.
     *
     * @param document the document
     * @return the title
     */
    protected abstract String getTitle(Document document);

    /**
     * Gets tags.
     *
     * @param document the document
     * @return the tags
     */
    protected abstract Set<String> getTags(Document document);

    /**
     * Gets date.
     *
     * @param document the document
     * @return the date
     */
    protected abstract String getDate(Document document);

    /**
     * Gets content.
     *
     * @param document the document
     * @return the content
     */
    protected abstract String getContent(Document document);

    /**
     * Gets date format.
     *
     * @return the date format
     */
    protected abstract String getDateFormat();

    /**
     * Gets parsable page.
     *
     * @return the parsable page
     */
    public abstract String getParsablePage();

    /**
     * Parse optional.
     *
     * @param data the data
     * @return the optional
     */
    public Optional<ArticleDTO> parse(final HtmlParseData data) {
        final String htmlData = data.getHtml().replaceAll("(\\r|\\n)", "");
        final Document document = Jsoup.parse(htmlData);
        final String content = getContent(document);
        final String date = getDate(document);
        final String title = getTitle(document);

        if (content.isEmpty()) {
            return Optional.empty();
        }

        final Set<Tag> tags = getTags(document).stream().map(tagString -> Tag.builder()
                .content(tagString).build())
                .collect(Collectors.toSet());

        final ArticleDTO article = new ArticleDTO();
        article.setContent(content);
        article.setTitle(title);
        article.setTags(tags);
        article.setDate(getLocalDate(date));
        return Optional.of(article);
    }

    private LocalDate getLocalDate(String date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(getDateFormat());
        try {
            return LocalDate.parse(date, formatter);
        } catch (final Exception e) {
            return LocalDate.now();
        }
    }
}
