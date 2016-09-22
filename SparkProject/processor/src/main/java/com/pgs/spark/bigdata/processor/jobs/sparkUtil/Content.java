package com.pgs.spark.bigdata.processor.jobs.sparkUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The content type for spark dataframes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content implements Serializable {

    private static final long serialVersionUID = 6301485118661321762L;

    private String id;

    private String documentId;

    private String searchProfileId;

    private String text;

}
