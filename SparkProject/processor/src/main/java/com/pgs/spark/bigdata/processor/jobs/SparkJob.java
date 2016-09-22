package com.pgs.spark.bigdata.processor.jobs;

import com.pgs.spark.bigdata.processor.domain.Classification;
import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.dto.AlgorithmEstimationDTO;
import com.pgs.spark.bigdata.processor.dto.ResultPKDTO;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.Content;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.LabeledContent;
import com.pgs.spark.bigdata.processor.mappers.ResultMappers;
import com.pgs.spark.bigdata.processor.repository.ResultRepository;
import com.pgs.spark.bigdata.processor.repository.util.PageRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.spark.ml.Estimator;
import org.apache.spark.ml.Model;
import org.apache.spark.ml.feature.HashingTF;
import org.apache.spark.ml.feature.Tokenizer;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.pgs.spark.bigdata.processor.utils.RowUtils.uuidFromRow;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * The type Spark job.
 */
@PropertySource("classpath:application.properties")
public abstract class SparkJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkJob.class);

    private static final int SQL_QUERY_LIMIT = 1000;

    private static final int NUM_FEATURES_POWER = 10;

    private Double trainingRatio = 1.0;

    protected final ExecutorService executor = Executors.newFixedThreadPool(100);

    /**
     * The Num features table.
     */
    @Value("#{'${algorithms.numFeaturesTable}'.split(',')}")
    protected int[] numFeaturesTable;

    /**
     * The Reg param table.
     */
    @Value("#{'${algorithms.regParamTable}'.split(',')}")
    protected double[] regParamTable;

    /**
     * The Elastic net param table.
     */
    @Value("#{'${algorithms.elasticNetParamTable}'.split(',')}")
    protected double[] elasticNetParamTable;

    /**
     * The Max iter table.
     */
    @Value("#{'${algorithms.maxIterTable}'.split(',')}")
    protected int[] maxIterTable;

    /**
     * The Sql context.
     */
    @Autowired
    protected SQLContext sqlContext;

    /**
     * The Result repository.
     */
    @Autowired
    protected ResultRepository resultRepository;

    @Autowired
    private ResultMappers resultMappers;

    /**
     * Gets max features.
     *
     * @return the max features
     */
    protected int getMaxFeatures() {
        return (int) Math.pow(2, NUM_FEATURES_POWER);
    }

    /**
     * Gets tokenizer.
     *
     * @return the tokenizer
     */
    protected Tokenizer getTokenizer() {
        return new Tokenizer()
                .setInputCol("text")
                .setOutputCol("words");
    }

    /**
     * Gets hashing tf.
     *
     * @param tokenizer the tokenizer
     * @return the hashing tf
     */
    protected HashingTF getHashingTF(final Tokenizer tokenizer) {
        return new HashingTF()
                .setNumFeatures(getMaxFeatures())
                .setInputCol(tokenizer.getOutputCol())
                .setOutputCol("features");
    }

    /**
     * Run.
     *
     * @param searchProfileId the search profile id
     */
    public abstract void run(final UUID searchProfileId);

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    protected abstract Estimator<?> getConfiguration();

    /**
     * Perform classification of results and return predictions as DTO,
     * this method also counts algorithms accuracy and sets given fields in DTO
     *
     * @param searchProfileId the id of search profile
     * @return the predictions
     */
    public CompletableFuture<Optional<AlgorithmEstimationDTO>> getPredictions(final UUID searchProfileId) {
        final Estimator<?> estimator = getConfiguration();
        if (isNull(estimator))
            return CompletableFuture.completedFuture(Optional.empty());

        final Map<UUID, Double> resultPredictionMap = new HashMap<>();
        final DataFrame dataFrame = getTrainingData(searchProfileId);
        final DataFrame[] dataFrameSplits = dataFrame.randomSplit(new double[]{trainingRatio, 1 - trainingRatio});
        final DataFrame trainingData = dataFrameSplits[0];
        final DataFrame validationData = dataFrameSplits[1];

        return CompletableFuture.supplyAsync(() -> {
            final Model<?> model = estimator.fit(trainingData);

            final Long numberOfCorrectlyEstimated = getCountOfCorrectlyEstimated(validationData, model);
            List<Result> list = resultRepository.findBySearchProfileToEstimating(searchProfileId, new PageRequest(null, SQL_QUERY_LIMIT));

            while (CollectionUtils.isNotEmpty(list)) {
                final DataFrame testData = sqlContext.createDataFrame(list
                        .stream()
                        .map(resultMappers::toContent)
                        .collect(Collectors.toList()), Content.class);

                final DataFrame predictions = model.transform(testData);
                UUID lastUuid = null;
                for (final Classification classification : Classification.values()) {
                    final List<ResultPKDTO> uuids = predictions.select("id", "documentId", "searchProfileId", "prediction").collectAsList()
                            .stream()
                            .filter(row -> classification.equals(Classification.fromLabelLinear(row.getDouble(3))))
                            .map(row -> {
                                ResultPKDTO resultPKDTO = toResultPKDTO(row);
                                resultPredictionMap.put(resultPKDTO.getResultId(), row.getDouble(3));
                                return resultPKDTO;
                            })
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(uuids)) {
                        lastUuid = uuids.get(uuids.size() - 1).getResultId();
                    }
                }
                list = resultRepository.findBySearchProfileToEstimating(searchProfileId, new PageRequest(lastUuid, SQL_QUERY_LIMIT));
            }
            int estimationAccuracy = validationData.count() == numberOfCorrectlyEstimated ? 100 : (int) Math.ceil(100 * numberOfCorrectlyEstimated.floatValue() / validationData.count());
            return Optional.of(AlgorithmEstimationDTO.builder()
                    .resultPredictionMap(resultPredictionMap)
                    .trainingRatio((int) (trainingRatio * 100))
                    .estimationAccuracy((int) Math.ceil(estimationAccuracy))
                    .build());
        }, executor);
    }

    /**
     * Create estimation model and save predictions in database.
     *
     * @param searchProfileId the search profile id
     */
    protected void createModelAndSavePredictions(final UUID searchProfileId) {
        final Estimator<?> estimator = getConfiguration();
        if (nonNull(estimator)) {
            executor.submit(() -> {
                final DataFrame dataFrame = getTrainingData(searchProfileId);
                final Model<?> model = estimator.fit(dataFrame);
                List<Result> list = resultRepository.findBySearchProfileToEstimating(searchProfileId, new PageRequest(null, SQL_QUERY_LIMIT));
                while (CollectionUtils.isNotEmpty(list)) {
                    final DataFrame testData = sqlContext.createDataFrame(list
                            .stream()
                            .map(resultMappers::toContent)
                            .collect(Collectors.toList()), Content.class);

                    final DataFrame predictions = model.transform(testData);

                    UUID lastUuid = null;

                    for (final Classification classification : Classification.values()) {
                        final List<ResultPKDTO> uuids = predictions.select("id", "documentId", "searchProfileId", "prediction").collectAsList()
                                .stream()
                                .filter(row -> classification.equals(Classification.fromLabelLinear(row.getDouble(3))))
                                .map(this::toResultPKDTO)
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(uuids)) {
                            resultRepository.updateClassification(uuids, classification);
                            lastUuid = uuids.get(uuids.size() - 1).getResultId();
                        }
                    }
                    list = resultRepository.findBySearchProfileToEstimating(searchProfileId, new PageRequest(lastUuid, SQL_QUERY_LIMIT));
                }
            });
        }
    }

    private synchronized Long getCountOfCorrectlyEstimated(final DataFrame validationData, final Model<?> model) {
        final Map<UUID, Double> resultClassificationMap = new HashMap<>();
        for (Row row : validationData.select("id", "label").collect()) {
            resultClassificationMap.put(UUID.fromString(row.getString(0)), row.getDouble(1));
        }

        final DataFrame predictions = model.transform(validationData);
        return predictions.select("id", "prediction").collectAsList()
                .stream()
                .filter(row -> {
                    final UUID resultId = uuidFromRow(row, 0);
                    return resultClassificationMap.get(resultId).equals(Classification.fromLabelLinear(row.getDouble(1)).getSparkLabel());
                }).count();
    }

    /**
     * Gets training data.
     *
     * @param searchProfileId the search profile id
     * @return the training data
     */
    protected DataFrame getTrainingData(final UUID searchProfileId) {
        final List<Result> trainingData = resultRepository.findBySearchProfileToTraining(searchProfileId);
        return sqlContext.createDataFrame(trainingData
                .stream()
                .map(resultMappers::toLabeledContent)
                .collect(Collectors.toList()), LabeledContent.class);
    }

    /**
     * Set training-validation ratio for given spark job.
     *
     * @param trainingRatio the training ratio
     * @return the spark job
     */
    public SparkJob withTrainingRatio(final Integer trainingRatio) {
        this.trainingRatio = Optional.ofNullable(trainingRatio).orElse(100).doubleValue() / 100;
        return this;
    }

    private ResultPKDTO toResultPKDTO(Row row) {
        return new ResultPKDTO(UUID.fromString(row.getString(0)), UUID.fromString(row.getString(1)), UUID.fromString(row.getString(2)));
    }
}