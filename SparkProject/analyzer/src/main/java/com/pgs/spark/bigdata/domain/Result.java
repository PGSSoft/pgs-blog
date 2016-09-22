package com.pgs.spark.bigdata.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "results")
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

    public Result() {
    }

    private Result(UUID id, Boolean isTrainingData, Classification classification, UUID searchProfileId, UUID documentId,
                   String documentUrl, String documentContent, LocalDate documentDate) {
        this.id = id;
        this.isTrainingData = isTrainingData;
        this.classification = classification;
        this.searchProfileId = searchProfileId;
        this.documentId = documentId;
        this.documentUrl = documentUrl;
        this.documentContent = documentContent;
        this.documentDate = documentDate;
    }

    public static Result unclassifiedResult(UUID searchProfileId, UUID documentId, String documentUrl, String documentContent, LocalDate documentDate) {
        return new Result(null, Boolean.FALSE, null, searchProfileId, documentId, documentUrl, documentContent, documentDate);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getIsTrainingData() {
        return isTrainingData;
    }

    public void setIsTrainingData(Boolean trainingData) {
        isTrainingData = trainingData;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public UUID getSearchProfileId() {
        return searchProfileId;
    }

    public void setSearchProfileId(UUID searchProfileId) {
        this.searchProfileId = searchProfileId;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }

    public Boolean getTrainingData() {
        return isTrainingData;
    }

    public void setTrainingData(Boolean trainingData) {
        isTrainingData = trainingData;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final Result rhs = (Result) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.isTrainingData, rhs.isTrainingData)
            .append(this.classification, rhs.classification)
            .append(this.searchProfileId, rhs.searchProfileId)
            .append(this.documentId, rhs.documentId)
            .append(this.documentUrl, rhs.documentUrl)
            .append(this.documentContent, rhs.documentContent)
            .append(this.documentDate, rhs.documentDate)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(isTrainingData)
            .append(classification)
            .append(searchProfileId)
            .append(documentId)
            .append(documentUrl)
            .append(documentContent)
            .append(documentDate)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("isTrainingData", isTrainingData)
            .append("classification", classification)
            .append("searchProfileId", searchProfileId)
            .append("documentId", documentId)
            .append("documentUrl", documentUrl)
            .append("documentContent", documentContent)
            .append("documentDate", documentDate)
            .toString();
    }

    public static Builder bulder() {
        return new Builder();
    }

    public static class Builder {

        private UUID id;
        private Boolean isTrainingData;
        private UUID searchProfileId;
        private UUID documentId;
        private String documentUrl;
        private String documentContent;
        private LocalDate documentDate;
        private Classification classification;

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder isTrainingData(final boolean isTrainingData) {
            this.isTrainingData = isTrainingData;
            return this;
        }

        public Builder searchProfileId(final UUID searchProfileId) {
            this.searchProfileId = searchProfileId;
            return this;
        }

        public Builder documentId(final UUID documentId) {
            this.documentId = documentId;
            return this;
        }

        public Builder documentUrl(final String documentUrl) {
            this.documentUrl = documentUrl;
            return this;
        }

        public Builder documentContent(final String documentContent) {
            this.documentContent = documentContent;
            return this;
        }

        public Builder documentDate(final LocalDate documentDate) {
            this.documentDate = documentDate;
            return this;
        }

        public Builder classification(final Classification classification) {
            this.classification = classification;
            return this;
        }

        public Result build() {
            Result result = new Result();
            result.setId(id);
            result.setDocumentId(documentId);
            result.setDocumentContent(documentContent);
            result.setDocumentDate(documentDate);
            result.setDocumentUrl(documentUrl);
            result.setIsTrainingData(isTrainingData);
            result.setSearchProfileId(searchProfileId);
            result.setClassification(classification);
            return result;
        }
    }
}
