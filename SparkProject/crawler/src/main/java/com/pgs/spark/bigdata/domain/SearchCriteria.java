package com.pgs.spark.bigdata.domain;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * The type Search criteria.
 */
@Table(name = "search_criteria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria extends CassandraEntity {
    private static final long serialVersionUID = -5237753769600692269L;

    @PartitionKey
    private UUID id;

    @NotNull
    @Column(name = "search_profile_id")
    @ClusteringColumn
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

}
