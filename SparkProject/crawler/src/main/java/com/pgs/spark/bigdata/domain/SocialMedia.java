package com.pgs.spark.bigdata.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The enum Social media.
 */
public enum SocialMedia {
    /**
     * Twitter social media.
     */
    TWITTER;

    private static final String serviceSuffix = "CrawlingService";

    /**
     * String values list.
     *
     * @return the list
     */
    public static List<String> stringValues() {
        List<String> stringValues = new ArrayList<>();
        for (SocialMedia socialMedia : SocialMedia.values()) {
            stringValues.add(socialMedia.name());
        }
        return stringValues;
    }

    /**
     * From class optional.
     *
     * @param simpleName the simple name
     * @return the optional
     */
    public static Optional<SocialMedia> fromClass(String simpleName) {
        try {
            return Optional.of(SocialMedia.valueOf(simpleName.substring(0, simpleName.indexOf(serviceSuffix)).toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
