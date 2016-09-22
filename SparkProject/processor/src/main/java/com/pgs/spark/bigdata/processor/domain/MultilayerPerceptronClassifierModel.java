package com.pgs.spark.bigdata.processor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * The type Multilayer perceptron classifier model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultilayerPerceptronClassifierModel {

    private Integer featuresSize;

    private Integer outputClassesSize;

    private int[] hiddenLayers;

    public int[] buildLayersArray() {
        final int[] result = new int[hiddenLayers.length + 2];
        //first element set to features size
        result[0] = featuresSize;
        //fill hidden layers
        System.arraycopy(hiddenLayers, 0, result, 1, hiddenLayers.length);
        //last element set to ouput classes size
        result[result.length - 1] = outputClassesSize;
        return result;
    }

}
