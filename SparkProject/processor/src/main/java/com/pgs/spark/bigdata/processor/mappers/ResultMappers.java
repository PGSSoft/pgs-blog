package com.pgs.spark.bigdata.processor.mappers;

import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.Content;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.LabeledContent;
import org.springframework.stereotype.Component;

/**
 * The type Result mappers.
 */
@Component
public class ResultMappers {

    /**
     * To content content.
     *
     * @param result the result
     * @return the content
     */
    public Content toContent(Result result) {
        return new Content(result.getId().toString(), result.getDocumentId().toString(), result.getSearchProfileId().toString(), result.getDocumentContent());
    }

    /**
     * To labeled content labeled content.
     *
     * @param result the result
     * @return the labeled content
     */
    public LabeledContent toLabeledContent(Result result) {
        return new LabeledContent(result.getId().toString(), result.getDocumentId().toString(), result.getSearchProfileId().toString(), result.getDocumentContent(), result.getClassification().getSparkLabel());
    }
}
