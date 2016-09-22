package com.pgs.spark.bigdata.processor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.spark.ml.Model;

import java.util.Map;
import java.util.UUID;

/**
 * The type Algorithm estimation dto.
 */
@AllArgsConstructor
@Builder
public class AlgorithmEstimationDTO {

    @Getter
    private final Map<UUID, Double> resultPredictionMap;
    @Getter
    private final Integer trainingRatio;
    @Getter
    private final Integer estimationAccuracy;

}
