package com.pgs.spark.bigdata.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String classification;

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

}
