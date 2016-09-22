package com.pgs.spark.bigdata.processor.domain;

import java.util.Optional;

/**
 * The enum Algorithm.
 */
public enum Algorithm {
    /**
     * Cross validation algorithm.
     */
    CROSS_VALIDATION("CrossValidationEstimationJob"), /**
     * Simple algorithm.
     */
    SIMPLE("SimpleLogisticRegressionJob"), /**
     * Multilayer perceptron algorithm.
     */
    MULTILAYER_PERCEPTRON("MultilayerPerceptronClassifierJob"),
    /**
     * Train validation algorithm.
     */
    TRAIN_VALIDATION("TrainValidationEstimationJob");

    private final String normalizedName;

    Algorithm(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    /**
     * Gets normalized name.
     *
     * @return the normalized name
     */
    public String getNormalizedName() {
        return normalizedName;
    }

    /**
     * From value optional.
     *
     * @param name the name
     * @return the optional
     */
    public static Optional<Algorithm> fromValue(String name) {
        for (Algorithm algorithm : values()) {
            if (algorithm.getNormalizedName().equals(name)) {
                return Optional.of(algorithm);
            }
        }
        return Optional.empty();
    }
}
