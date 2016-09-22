package com.pgs.spark.bigdata.processor.domain;

import org.apache.commons.lang.math.DoubleRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Supplier;

/**
 * The enum Classification.
 */
public enum Classification implements Serializable {
    /**
     * Positive classification.
     */
    POSITIVE(2.0),
    /**
     * Negative classification.
     */
    NEGATIVE(0.0);

    private static final Logger LOGGER = LoggerFactory.getLogger(Classification.class);

    private static Classification MIN;

    private static Classification MAX;

    private final double sparkLabel;

    Classification(double sparkLabel) {
        this.sparkLabel = sparkLabel;
    }

    /**
     * Gets spark label.
     *
     * @return the spark label
     */
    public double getSparkLabel() {
        return this.sparkLabel;
    }

    /**
     * From label linear classification.
     *
     * @param label the label
     * @return the classification
     */
    public static Classification fromLabelLinear(double label) {
        return Arrays.stream(values())
                .filter(c -> new DoubleRange(c.sparkLabel - 1.0, c.sparkLabel + 1.0).containsDouble(label))
                .findFirst()
                .orElseGet(handleExtremeSituation(label));
    }

    /**
     * Gets maximum.
     *
     * @return the maximum
     */
    public static Classification getMaximum() {
        if (MAX == null) {
            //noinspection OptionalGetWithoutIsPresent
            MAX = Arrays.stream(values())
                    .max((c1, c2) -> Double.valueOf(c1.getSparkLabel()).compareTo(c2.getSparkLabel()))
                    .get();
        }
        return MAX;
    }

    /**
     * Gets minimum.
     *
     * @return the minimum
     */
    public static Classification getMinimum() {
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
