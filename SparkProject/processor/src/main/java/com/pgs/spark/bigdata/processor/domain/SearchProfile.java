package com.pgs.spark.bigdata.processor.domain;

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
import java.util.UUID;

/**
 * The type Search profile.
 */
@Table(name = "search_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProfile extends CassandraEntity {

    private static final long serialVersionUID = 3374621792184919487L;

    @PartitionKey
    private UUID id;

    @NotNull
    private String name;

}
