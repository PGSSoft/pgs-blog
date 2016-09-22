package com.pgs.spark.bigdata.processor.domain;

import org.junit.Assert;
import org.junit.Test;

public class MultilayerPerceptronClassifierModelTest {

    @Test
    public void testBuildLayersArray() {
        final MultilayerPerceptronClassifierModel model = new MultilayerPerceptronClassifierModel();
        model.setFeaturesSize(1);
        model.setHiddenLayers(new int[]{2, 3, 4, 5});
        model.setOutputClassesSize(6);

        Assert.assertArrayEquals(model.buildLayersArray(), new int[]{1, 2, 3, 4, 5, 6});
    }

}
