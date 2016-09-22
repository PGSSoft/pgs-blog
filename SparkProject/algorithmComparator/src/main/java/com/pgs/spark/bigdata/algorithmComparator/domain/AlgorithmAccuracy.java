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

import java.io.Serializable;
import java.util.UUID;

@Table(name = "algorithm_accuracy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmAccuracy extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = 1447295616635595840L;

    @PartitionKey
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "accuracy")
    private Integer accuracy;

    @Column(name = "search_profile_id")
    private UUID searchProfileId;

}
