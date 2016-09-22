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
 * The type Document.
 */
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document extends CassandraEntity {

    private static final long serialVersionUID = 2004546918548764995L;

    @PartitionKey
    private UUID id;

    @Column
    private String content = "";

    @NotNull
    private String url = "";

    @NotNull
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Column(name = "title")
    private String title = "";

    @Column(name = "tags")
    private String tags = "";

    @Column(name = "classification")
    private String classification = "";

}
