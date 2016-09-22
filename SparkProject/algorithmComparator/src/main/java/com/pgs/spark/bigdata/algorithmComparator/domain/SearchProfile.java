package com.pgs.spark.bigdata.algorithmComparator.domain;

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

@Table(name = "search_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProfile extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = -5753323397126578602L;

    @PartitionKey
    private UUID id;

    @NotNull
    private String name;

}
