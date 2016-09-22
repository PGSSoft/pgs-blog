package com.pgs.spark.bigdata.algorithmComparator.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public class HeaderUtil {

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-analyzerApp-alert", message);
        headers.add("X-analyzerApp-params", param);
        return headers;
    }
}
