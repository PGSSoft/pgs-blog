package com.pgs.spark.bigdata.processor.jobs.estimationJobs;

import com.pgs.spark.bigdata.processor.jobs.SparkJob;

import java.util.UUID;

public abstract class SparkEstimationJob extends SparkJob {
    @Override
    public void run(UUID searProfileId) {
        createModelAndSavePredictions(searProfileId);
    }
}
