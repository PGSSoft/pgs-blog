package com.pgs.spark.bigdata.algorithmComparator.domain;

import java.util.Optional;

public enum Algorithm {
    CROSS_VALIDATION("CrossValidationEstimationJob"),
    SIMPLE("SimpleLogisticRegressionJob"),
    MULTILAYER_PERCEPTRON("MultilayerPerceptronClassifierJob"),
    TRAIN_VALIDATION("TrainValidationEstimationJob");

    private final String normalizedName;

    Algorithm(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public static Optional<Algorithm> fromValue(String name) {
        for (Algorithm algorithm : values()) {
            if (algorithm.getNormalizedName().equals(name)) {
                return Optional.of(algorithm);
            }
        }
        return Optional.empty();
    }
}
