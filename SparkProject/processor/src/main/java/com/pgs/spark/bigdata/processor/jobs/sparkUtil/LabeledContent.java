package com.pgs.spark.bigdata.processor.jobs.sparkUtil;

import lombok.Data;

/**
 * The labeled content type for spark dataframes.
 */
@Data
public class LabeledContent extends Content {
    private static final long serialVersionUID = -1318982927614247167L;
    private double label;

    /**
     * Instantiates a new Labeled content.
     *
     * @param id              the id
     * @param documentId      the document id
     * @param searchProfileId the search profile id
     * @param text            the text
     * @param label           the label
     */
    public LabeledContent(final String id, final String documentId, final String searchProfileId, final String text, final double label) {
        super(id, documentId, searchProfileId, text);
        this.label = label;
    }
}
