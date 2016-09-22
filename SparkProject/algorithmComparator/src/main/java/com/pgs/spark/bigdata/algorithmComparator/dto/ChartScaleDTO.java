package com.pgs.spark.bigdata.algorithmComparator.dto;

public enum ChartScaleDTO {
    DAYS("Days", "dd/MM/yy"),
    WEEKS("Weeks", "ww/yy"),
    MONTHS("Months", "MM/yy"),
    YEARS("Years", "yyyy");

    private final String description;
    private final String pattern;

    ChartScaleDTO(final String description, final String pattern) {
        this.description = description;
        this.pattern = pattern;
    }

    public String getDescription() {
        return description;
    }

    public String getPattern() {
        return pattern;
    }

}
