package com.pgs.spark.bigdata.processor.jobs.tagClassificationJobs;


import com.pgs.spark.bigdata.processor.jobs.SparkJob;
import com.pgs.spark.bigdata.processor.service.TagClassificationService;
import org.apache.spark.sql.DataFrame;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * The type Tag classification spark job.
 */
public abstract class TagClassificationSparkJob extends SparkJob {

    @Autowired
    private TagClassificationService tagClassificationService;

    @Override
    protected DataFrame getTrainingData(final UUID searchProfileId) {
        return tagClassificationService.getBySuperTags(searchProfileId);
    }

}
