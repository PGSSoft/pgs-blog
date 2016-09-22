package com.pgs.spark.bigdata.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * The type Tag.
 */
@Table(name = "tags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = 7152805838597912530L;

    /**
     * Instantiates a new Tag.
     */
    /*public Tag() {
    }

    *//**
     * Instantiates a new Tag.
     *
     * @param id         the id
     * @param documentId the document id
     * @param content    the content
     *//*
    public Tag(final UUID id, final UUID documentId, final String content) {
        this.id = id;
        this.documentId = documentId;
        this.content = content;
    }*/

    @PartitionKey
    private UUID id;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "content")
    private String content;

    /*@Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    *//**
     * Gets document id.
     *
     * @return the document id
     *//*
    public UUID getDocumentId() {
        return documentId;
    }

    *//**
     * Sets document id.
     *
     * @param documentId the document id
     *//*
    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    *//**
     * Gets content.
     *
     * @return the content
     *//*
    public String getContent() {
        return content;
    }

    *//**
     * Sets content.
     *
     * @param content the content
     *//*
    public void setContent(String content) {
        this.content = content;
    }

    *//**
     * Builder builder.
     *
     * @return the builder
     *//*
    public static Builder builder() {
        return new Builder();
    }

    *//**
     * The type Builder.
     *//*
    public static class Builder {
        private UUID id;
        private UUID documentId;
        private String content;

        *//**
         * With id builder.
         *
         * @param id the id
         * @return the builder
         *//*
        public Builder withId(final UUID id) {
            this.id = id;
            return this;
        }

        *//**
         * With document id builder.
         *
         * @param documentId the document id
         * @return the builder
         *//*
        public Builder withDocumentId(final UUID documentId) {
            this.documentId = documentId;
            return this;
        }

        *//**
         * With content builder.
         *
         * @param content the content
         * @return the builder
         *//*
        public Builder withContent(final String content) {
            this.content = content;
            return this;
        }

        *//**
         * Build tag.
         *
         * @return the tag
         *//*
        public Tag build() {
            return new Tag(id, documentId, content);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final Tag tag = (Tag) o;

        return new EqualsBuilder()
                .append(id, tag.id)
                .append(documentId, tag.documentId)
                .append(content, tag.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(documentId)
                .append(content)
                .toHashCode();
    }*/
}
