package com.pgs.spark.bigdata.processor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The type Multilayer perceptron classifier properties.
 */
@ConfigurationProperties(value = "multilayerPerceptronClassifier")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultilayerPerceptronClassifierProperties {

    private int outputClassNumber;
    private int maxIter;
    private long seed;
    private int blockSize;
    private int featuresSize;
    private int[] hiddenLayers;

}
