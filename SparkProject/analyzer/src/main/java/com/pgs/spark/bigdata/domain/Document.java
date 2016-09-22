package com.pgs.spark.bigdata.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "documents")
public class Document extends CassandraEntity {

    private static final long serialVersionUID = 2004546918548764995L;

    @PartitionKey
    private UUID id;

    @Column
    private String content;

    @NotNull
    private String url;

    @NotNull
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
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
        final Document rhs = (Document) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.content, rhs.content)
            .append(this.url, rhs.url)
            .append(this.creationDate, rhs.creationDate)
            .append(this.updateDate, rhs.updateDate)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(id)
            .append(content)
            .append(url)
            .append(creationDate)
            .append(updateDate)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("content", content)
            .append("url", url)
            .append("creationDate", creationDate)
            .append("updateDate", updateDate)
            .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String content;
        private String url;
        private LocalDate creationDate;
        private LocalDate updateDate;

        public Builder id(final UUID id) {
            this.id = id;
            return this;
        }

        public Builder content(final String content) {
            this.content = content;
            return this;
        }

        public Builder url(final String url) {
            this.url = url;
            return this;
        }

        public Builder creationDate(final LocalDate creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder updateDate(final LocalDate updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Document build() {
            Document document = new Document();
            document.setId(id);
            document.setContent(content);
            document.setUrl(url);
            document.setCreationDate(creationDate);
            document.setUpdateDate(updateDate);
            return document;
        }
    }
}
