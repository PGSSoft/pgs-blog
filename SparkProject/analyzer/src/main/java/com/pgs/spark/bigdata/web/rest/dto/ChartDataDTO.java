package com.pgs.spark.bigdata.web.rest.dto;

import com.google.common.collect.Lists;
import com.google.common.primitives.Longs;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChartDataDTO implements Serializable {

    private static final long serialVersionUID = -7228358001391455906L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChartDataDTO.class);

    private List<String> labels;

    private List<Classification> series = Lists.newArrayList(Classification.values());

    private List<List<Long>> data;

    public void addLabel(String label) {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        labels.add(label);
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(final List<String> labels) {
        this.labels = labels;
    }

    public List<Classification> getSeries() {
        return series;
    }

    public void setSeries(final List<Classification> series) {
        this.series = series;
    }

    public void addDataSet(long[] dataSet) {
        addDataSet(Longs.asList(dataSet));
    }

    public void addDataSet(List<Long> dataSet) {
        if (data == null) {
            data = new ArrayList<>();
        }
        if ((dataSet != null) && (dataSet.size() == labels.size())) {
            data.add(dataSet);
        } else {
            LOGGER.warn("Attended to append non-compatible data set.");
        }
    }

    public List<List<Long>> getData() {
        return data;
    }

    public void setData(final List<List<Long>> data) {
        this.data = data;
    }
}
