package com.pgs.spark.bigdata.processor.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The type Result.
 */
@Table(name = "results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result extends CassandraEntity {
    private static final long serialVersionUID = -4900109403108765787L;

    @PartitionKey
    private UUID id;

    @Column(name = "is_training_data")
    private Boolean isTrainingData;

    @Column
    private Classification classification;

    @NotNull
    @Column(name = "search_profile_id")
    private UUID searchProfileId;

    @NotNull
    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "document_url")
    private String documentUrl;

    @Column(name = "document_content")
    private String documentContent;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "tags")
    private String tags;

    @Column(name = "parent_tag")
    private UUID parentTag;

    /**
     * Unclassified result result.
     *
     * @param searchProfileId the search profile id
     * @param documentId      the document id
     * @param documentUrl     the document url
     * @param documentContent the document content
     * @param documentDate    the document date
     * @param tags            the tags
     * @return the result
     */
    public static Result unclassifiedResult(UUID searchProfileId, UUID documentId, String documentUrl, String documentContent, LocalDate documentDate, String tags) {
        return new Result(null, Boolean.FALSE, null, searchProfileId, documentId, documentUrl, documentContent, documentDate, tags, null);
    }

    /**
     * Classified result result.
     *
     * @param searchProfileId the search profile id
     * @param documentId      the document id
     * @param documentUrl     the document url
     * @param documentContent the document content
     * @param documentDate    the document date
     * @param tags            the tags
     * @param classification  the classification
     * @return the result
     */
    public static Result classifiedResult(UUID searchProfileId, UUID documentId, String documentUrl, String documentContent, LocalDate documentDate, String tags, Classification classification) {
        final Result result = new Result(null, Boolean.TRUE, null, searchProfileId, documentId, documentUrl, documentContent, documentDate, tags, null);
        result.setClassification(classification);
        return result;
    }

}
