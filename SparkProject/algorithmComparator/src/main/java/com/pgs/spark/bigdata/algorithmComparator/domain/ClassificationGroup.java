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

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "classification_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationGroup extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = -385552271385509484L;

    @PartitionKey
    private UUID id;

    @NotNull
    @Column(name = "result_id")
    private UUID resultId;

    @Column(name = "search_profile_id")
    private UUID searchProfileId;

    @Column(name = "simple_classification")
    private Classification simpleClassification;

    @Column(name = "cross_classification")
    private Classification crossValidatorClassification;

    @Column(name = "train_classification")
    private Classification trainValidatorClassification;

    @Column(name = "multilayer_classification")
    private Classification multilayerPerceptronClassification;

    @Column(name = "document_date")
    private LocalDate documentDate;

}
