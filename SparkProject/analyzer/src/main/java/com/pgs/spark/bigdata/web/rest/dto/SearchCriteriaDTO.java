package com.pgs.spark.bigdata.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


/**
 * A DTO for the SearchCriteria entity.
 */
public class SearchCriteriaDTO implements Serializable {

    private UUID id;

    @NotNull
    @Size(max = 255)
    private String keyWord;


    @Size(max = 255)
    private String mustHaveWord;


    @Size(max = 255)
    private String excludedWord;


    private UUID searchProfileId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getSearchProfileId() {
        return searchProfileId;
    }

    public void setSearchProfileId(UUID searchProfileId) {
        this.searchProfileId = searchProfileId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        final SearchCriteriaDTO searchCriteriaDTO = (SearchCriteriaDTO) o;

        if (!Objects.equals(id, searchCriteriaDTO.id)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SearchCriteriaDTO{" +
            "id=" + id +
            ", keyWord='" + keyWord + "'" +
            ", mustHaveWord='" + mustHaveWord + "'" +
            ", excludedWord='" + excludedWord + "'" +
            '}';
    }
}
