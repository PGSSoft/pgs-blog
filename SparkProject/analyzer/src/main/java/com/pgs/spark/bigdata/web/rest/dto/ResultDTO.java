package com.pgs.spark.bigdata.web.rest.dto;

import com.pgs.spark.bigdata.domain.enumeration.Classification;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the Result entity.
 */
public class ResultDTO implements Serializable {

    private UUID id;

    private Classification classification;

    private Boolean isTrainingData;

    private UUID documentId;

    private UUID searchProfileId;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Classification getClassification() {
        return classification;
    }

    public void setClassification(Classification classification) {
        this.classification = classification;
    }

    public Boolean getIsTrainingData() {
        return isTrainingData;
    }

    public void setIsTrainingData(Boolean isTrainingData) {
        this.isTrainingData = isTrainingData;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
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

        final ResultDTO resultDTO = (ResultDTO) o;

        if (!Objects.equals(id, resultDTO.id)) {
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
        return "ResultDTO{" + "id=" + id + ", classification='" + classification + "'" + ", isTrainingData='" + isTrainingData + "'" + '}';
    }
}
