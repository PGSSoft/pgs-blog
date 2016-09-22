package com.pgs.spark.bigdata.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * The type Result primary key dto.
 */
@AllArgsConstructor
public class ResultPKDTO implements Serializable {

    private static final long serialVersionUID = 8488674778950624457L;

    @Getter
    private final UUID resultId;
    @Getter
    private final UUID documentId;
    @Getter
    private final UUID searchProfileId;

}
