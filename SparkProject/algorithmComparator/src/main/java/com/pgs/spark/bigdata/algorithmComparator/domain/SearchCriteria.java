package com.pgs.spark.bigdata.algorithmComparator.domain;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
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
import java.util.UUID;

@Table(name = "search_criteria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = 5686637963109386756L;

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
