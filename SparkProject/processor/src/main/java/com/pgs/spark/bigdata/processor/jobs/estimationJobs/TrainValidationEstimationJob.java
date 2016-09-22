package com.pgs.spark.bigdata.processor.jobs.estimationJobs;

import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.springframework.stereotype.Component;


/**
 * The type Train validation estimation job.
 */
@Component
public class TrainValidationEstimationJob extends SparkEstimationJob {

    @Override
    protected Estimator<?> getConfiguration() {
        Tokenizer tokenizer = getTokenizer();
        HashingTF hashingTF = getHashingTF(tokenizer);

        final LinearRegression lr = new LinearRegression();

        final Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});

        final ParamMap[] paramGrid = new ParamGridBuilder()
                .addGrid(hashingTF.numFeatures(), numFeaturesTable)
                .addGrid(lr.regParam(), regParamTable)
                .addGrid(lr.fitIntercept())
                .addGrid(lr.elasticNetParam(), elasticNetParamTable)
                .build();

        return new TrainValidationSplit()
                .setEstimator(pipeline)
                .setEvaluator(new RegressionEvaluator())
                .setEstimatorParamMaps(paramGrid);
    }
}