package com.pgs.spark.bigdata.processor.jobs.tagClassificationJobs;

import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.domain.Tag;
import com.pgs.spark.bigdata.processor.utils.TagClassificationHolder;
import com.pgs.spark.bigdata.processor.service.TagClassificationService;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.ml.param.ParamMap;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.tuning.ParamGridBuilder;
import org.apache.spark.ml.tuning.TrainValidationSplit;
import org.apache.spark.sql.DataFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The type Basic tag classification spark job.
 */
@Component
public class BasicTagClassificationSparkJob extends TagClassificationSparkJob {

    @Autowired
    private TagClassificationService tagClassificationService;

    @Autowired
    private TagClassificationHolder tagClassificationHolder;

    @Override
    protected DataFrame getTrainingData(final UUID searchProfileId) {
        return tagClassificationService.getBySuperTags(searchProfileId);
    }

    private DataFrame getValidationData(final UUID searchProfileId) {
        return tagClassificationService.getWithoutSuperTag(searchProfileId);
    }

    @Override
    public void run(final UUID searchProfileId) {
        final DataFrame trainingData = getTrainingData(searchProfileId);
        final Estimator<?> estimator = getConfiguration();
        final DataFrame validationData = getValidationData(searchProfileId);

        final Model<?> model = estimator.fit(trainingData);
        final DataFrame results = model.transform(validationData);
        results.select("prediction", "id").collectAsList()
                .forEach(row -> {
                    final double prediction = row.getDouble(0);
                    final Tag tag = tagClassificationHolder.tagFromLabel(prediction);
                    final Result result = resultRepository.findOne(UUID.fromString(row.getString(1)));
                    result.setParentTag(tag.getId());
                    resultRepository.save(result);
                });
    }

    @Override
    protected synchronized Estimator<?> getConfiguration() {
        final Tokenizer tokenizer = getTokenizer();
        final HashingTF hashingTF = getHashingTF(tokenizer);

        final LinearRegression lr = new LinearRegression();

        final Pipeline pipeline = new Pipeline()
                .setStages(new PipelineStage[]{tokenizer, hashingTF, lr});

        final ParamMap[] paramGrid = new ParamGridBuilder()
                .addGrid(hashingTF.numFeatures(), new int[]{10, 15, 20})
                .addGrid(lr.regParam(), regParamTable).addGrid(lr.fitIntercept())
                .addGrid(lr.elasticNetParam(), elasticNetParamTable)
                .build();

        return new TrainValidationSplit()
                .setEstimator(pipeline)
                .setEvaluator(new RegressionEvaluator())
                .setEstimatorParamMaps(paramGrid);
    }
}
