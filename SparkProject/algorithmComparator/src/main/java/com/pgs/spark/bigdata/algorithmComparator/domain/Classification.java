package com.pgs.spark.bigdata.algorithmComparator.domain;

import org.apache.commons.lang.math.DoubleRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Supplier;

public enum Classification implements Serializable {
    POSITIVE(2.0),
    NEGATIVE(0.0);

    private static final Logger LOGGER = LoggerFactory.getLogger(Classification.class);

    private static Classification MIN;

    private static Classification MAX;

    private final double sparkLabel;

    Classification(double sparkLabel) {
        this.sparkLabel = sparkLabel;
    }

    private double getSparkLabel() {
        return this.sparkLabel;
    }

    public static Classification fromLabelLinear(double label) {
        final Classification classification = Arrays.stream(values())
                .filter(c -> new DoubleRange(c.sparkLabel - 1.0, c.sparkLabel + 1.0).containsDouble(label))
                .findFirst()
                .orElseGet(handleExtremeSituation(label));
        LOGGER.info("The label {} classified as {}({})", String.format("%7.4f", label), classification, classification.getSparkLabel());
        return classification;
    }

    private static Classification getMaximum() {
        if (MAX == null) {
            //noinspection OptionalGetWithoutIsPresent
            MAX = Arrays.stream(values())
                    .max((c1, c2) -> Double.valueOf(c1.getSparkLabel()).compareTo(c2.getSparkLabel()))
                    .get();
        }
        return MAX;
    }

    private static Classification getMinimum() {
        if (MIN == null) {
            //noinspection OptionalGetWithoutIsPresent
            MIN = Arrays.stream(values())
                    .min((c1, c2) -> Double.valueOf(c1.getSparkLabel()).compareTo(c2.getSparkLabel()))
                    .get();
        }
        return MIN;
    }

    private static Supplier<Classification> handleExtremeSituation(double label) {
        return label > getMaximum().getSparkLabel() ? Classification::getMaximum : Classification::getMinimum;
    }

}
