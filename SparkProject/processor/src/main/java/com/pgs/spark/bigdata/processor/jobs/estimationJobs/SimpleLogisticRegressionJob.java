package com.pgs.spark.bigdata.processor.jobs.estimationJobs;

import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.regression.LinearRegression;
import org.springframework.stereotype.Component;

/**
 * The type Simple logistic regression job.
 */
@Component
public class SimpleLogisticRegressionJob extends SparkEstimationJob {
    @Override
    protected Estimator<?> getConfiguration() {
        final Tokenizer tokenizer = getTokenizer();
        final HashingTF hashingTF = getHashingTF(tokenizer);

        final LinearRegression lr = new LinearRegression()
                .setMaxIter(10)
                .setRegParam(0.01);

        return new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});
    }

    @Override
    protected int getMaxFeatures() {
        return 30;
    }

}
