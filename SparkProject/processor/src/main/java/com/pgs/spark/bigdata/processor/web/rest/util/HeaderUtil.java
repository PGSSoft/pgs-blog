package com.pgs.spark.bigdata.processor.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public class HeaderUtil {

    /**
     * Create alert http headers.
     *
     * @param message the message
     * @param param   the param
     * @return the http headers
     */
    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-analyzerApp-alert", message);
        headers.add("X-analyzerApp-params", param);
        return headers;
    }

    /**
     * Create entity creation alert http headers.
     *
     * @param entityName the entity name
     * @param param      the param
     * @return the http headers
     */
    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("analyzerApp." + entityName + ".created", param);
    }

    /**
     * Create entity update alert http headers.
     *
     * @param entityName the entity name
     * @param param      the param
     * @return the http headers
     */
    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("analyzerApp." + entityName + ".updated", param);
    }

    /**
     * Create entity deletion alert http headers.
     *
     * @param entityName the entity name
     * @param param      the param
     * @return the http headers
     */
    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("analyzerApp." + entityName + ".deleted", param);
    }

    /**
     * Create failure alert http headers.
     *
     * @param entityName     the entity name
     * @param errorKey       the error key
     * @param defaultMessage the default message
     * @return the http headers
     */
    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-analyzerApp-error", "error." + errorKey);
        headers.add("X-analyzerApp-params", entityName);
        return headers;
    }
}
