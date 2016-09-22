package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.dto.ArticleDTO;

import java.time.LocalDate;
import java.util.Set;

/**
 * The interface Document service.
 */
public interface DocumentService {

    /**
     * Add document.
     *
     * @param url            the url
     * @param articleContent the article content
     */
    void addDocument(String url, ArticleDTO articleContent);

    /**
     * Add document.
     *
     * @param url          the url
     * @param content      the content
     * @param creationDate the creation date
     * @param updateDate   the update date
     * @return the document
     */
    Document addDocument(String url, String content, LocalDate creationDate, LocalDate updateDate);

    /**
     * Add document.
     *
     * @param url            the url
     * @param content        the content
     * @param creationDate   the creation date
     * @param updateDate     the update date
     * @param classification the classification
     * @return the document
     */
    Document addDocument(String url, String content, LocalDate creationDate, LocalDate updateDate, String classification);

    /**
     * Add document.
     *
     * @param url          the url
     * @param content      the content
     * @param creationDate the creation date
     * @param updateDate   the update date
     * @param tags         the tags
     * @return the document
     */
    Document addDocument(String url, String content, LocalDate creationDate, LocalDate updateDate, Set<Tag> tags);
}
