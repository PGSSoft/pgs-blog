package com.pgs.spark.bigdata.algorithmComparator.dto;

import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChartDataDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDataDTO.class);

    private List<String> labels;

    private final List<Classification> series = Lists.newArrayList(Classification.values());

    private List<List<Long>> data;

    private Integer accuracy;

    public void addDataSet(long[] dataSet) {
        addDataSet(Longs.asList(dataSet));
    }

    private void addDataSet(List<Long> dataSet) {
        if (data == null) {
            data = new ArrayList<>();
        }
        if ((dataSet != null) && (dataSet.size() == labels.size())) {
            data.add(dataSet);
        } else {
            LOGGER.warn("Attended to append non-compatible data set.");
        }
    }


}
