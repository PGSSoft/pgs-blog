package com.pgs.spark.bigdata.processor.jobs.estimationJobs;

import com.pgs.spark.bigdata.processor.domain.MultilayerPerceptronClassifierModel;
import com.pgs.spark.bigdata.processor.domain.MultilayerPerceptronClassifierProperties;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Multilayer perceptron classifier job.
 */
@Component
public class MultilayerPerceptronClassifierJob extends SparkEstimationJob {

    private static final String TEXT_COLUMN = "text";
    private static final String WORDS_COLUMN = "words";
    private static final String FEATURES_COLUMN = "features";

    @Autowired
    private MultilayerPerceptronClassifierProperties properties;

    @Override
    protected Estimator<?> getConfiguration() {
        final Tokenizer tokenizer = new Tokenizer()
                .setInputCol(TEXT_COLUMN)
                .setOutputCol(WORDS_COLUMN);
        final HashingTF hashingTF = new HashingTF()
                .setNumFeatures(properties.getFeaturesSize())
                .setInputCol(tokenizer.getOutputCol())
                .setOutputCol(FEATURES_COLUMN);

        final int[] layers = buildLayers();
        final MultilayerPerceptronClassifier lr = new MultilayerPerceptronClassifier()
                .setLayers(layers)
                .setBlockSize(properties.getBlockSize())
                .setSeed(properties.getSeed())
                .setMaxIter(properties.getMaxIter());

        return new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});
    }

    private int[] buildLayers() {
        final MultilayerPerceptronClassifierModel model = new MultilayerPerceptronClassifierModel();
        model.setFeaturesSize(properties.getFeaturesSize());
        model.setHiddenLayers(properties.getHiddenLayers());
        model.setOutputClassesSize(properties.getOutputClassNumber());
        return model.buildLayersArray();
    }
}
