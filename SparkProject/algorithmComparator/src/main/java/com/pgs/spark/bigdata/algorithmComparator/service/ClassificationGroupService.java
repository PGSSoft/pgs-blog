package com.pgs.spark.bigdata.algorithmComparator.service;

import com.pgs.spark.bigdata.algorithmComparator.dto.ChartDataDTO;
import com.pgs.spark.bigdata.algorithmComparator.dto.ChartScaleDTO;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * The interface Classification group service.
 */
public interface ClassificationGroupService {
    /**
     * Perform classification using all algorithms.
     *
     * @param searchProfileId the search profile id
     * @param trainingRatio   the training ratio
     */
    void performClassificationUsingAllAlgorithms(final UUID searchProfileId, final Integer trainingRatio);

    /**
     * Gets chart data.
     *
     * @param searchProfile the search profile
     * @param scale         the scale
     * @param from          the from
     * @param until         the until
     * @return the chart data
     */
    Map<String, ChartDataDTO> getChartData(UUID searchProfile, ChartScaleDTO scale, LocalDate from, LocalDate until);
}
