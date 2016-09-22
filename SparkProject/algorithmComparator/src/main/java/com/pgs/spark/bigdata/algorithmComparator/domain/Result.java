package com.pgs.spark.bigdata.algorithmComparator.domain;

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
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = -3723066801994373203L;

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

    public static Result unclassifiedResult(UUID searchProfileId, UUID documentId, String documentUrl, String documentContent, LocalDate documentDate) {
        return new Result(null, Boolean.FALSE, null, searchProfileId, documentId, documentUrl, documentContent, documentDate);
    }
}
