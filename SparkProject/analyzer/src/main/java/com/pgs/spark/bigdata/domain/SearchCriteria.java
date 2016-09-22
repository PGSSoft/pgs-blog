package com.pgs.spark.bigdata.domain;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table(name = "search_criteria")
public class SearchCriteria extends CassandraEntity {
    private static final long serialVersionUID = -5237753769600692269L;

    @PartitionKey
    private UUID id;

    @NotNull
    @Column(name = "search_profile_id")
    private UUID searchProfileId;

    @NotNull
    @Column(name = "key_word")
    private String keyWord;

    @Column(name = "musthave_word")
    private String mustHaveWord;

    @Column(name = "excluded_word")
    private String excludedWord;

    @Column(name = "search_profile_name")
    private String searchProfileName;

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSearchProfileId() {
        return searchProfileId;
    }

    public void setSearchProfileId(UUID searchProfileId) {
        this.searchProfileId = searchProfileId;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getMustHaveWord() {
        return mustHaveWord;
    }

    public void setMustHaveWord(String mustHaveWord) {
        this.mustHaveWord = mustHaveWord;
    }

    public String getExcludedWord() {
        return excludedWord;
    }

    public void setExcludedWord(String excludedWord) {
        this.excludedWord = excludedWord;
    }

    public String getSearchProfileName() {
        return searchProfileName;
    }

    public void setSearchProfileName(String searchProfileName) {
        this.searchProfileName = searchProfileName;
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
        final SearchCriteria rhs = (SearchCriteria) obj;
        return new EqualsBuilder()
            .append(this.id, rhs.id)
            .append(this.searchProfileId, rhs.searchProfileId)
            .append(this.keyWord, rhs.keyWord)
            .append(this.mustHaveWord, rhs.mustHaveWord)
            .append(this.excludedWord, rhs.excludedWord)
            .append(this.searchProfileName, rhs.searchProfileName)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(searchProfileId)
            .append(keyWord)
            .append(mustHaveWord)
            .append(excludedWord)
            .append(searchProfileName)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("searchProfileId", searchProfileId)
            .append("keyWord", keyWord)
            .append("mustHaveWord", mustHaveWord)
            .append("excludedWord", excludedWord)
            .append("searchProfileName", searchProfileName)
            .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private UUID id;

        private UUID searchProfileId;

        private String keyWord;

        private String mustHaveWord;

        private String excludedWord;

        private String searchProfileName;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder searchProfileId(UUID searchProfileId) {
            this.searchProfileId = searchProfileId;
            return this;
        }

        public Builder keyWord(String keyWord) {
            this.keyWord = keyWord;
            return this;
        }

        public Builder mustHaveKeyword(String mustHaveWord) {
            this.mustHaveWord = mustHaveWord;
            return this;
        }

        public Builder excludedWord(String excludedWord) {
            this.excludedWord = excludedWord;
            return this;
        }

        public Builder searchProfileName(String searchProfileName) {
            this.searchProfileName = searchProfileName;
            return this;
        }

        public SearchCriteria build() {
            SearchCriteria searchCriteria = new SearchCriteria();
            searchCriteria.setId(id);
            searchCriteria.setKeyWord(keyWord);
            searchCriteria.setExcludedWord(excludedWord);
            searchCriteria.setMustHaveWord(mustHaveWord);
            searchCriteria.setSearchProfileId(searchProfileId);
            searchCriteria.setSearchProfileName(searchProfileName);
            return searchCriteria;
        }
    }
}
