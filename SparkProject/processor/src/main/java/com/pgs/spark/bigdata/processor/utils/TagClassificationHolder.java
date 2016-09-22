package com.pgs.spark.bigdata.processor.utils;

import com.pgs.spark.bigdata.processor.domain.Tag;
import org.apache.commons.lang.math.DoubleRange;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The type Tag classification holder.
 */
@Component
public class TagClassificationHolder {

    private final Map<Tag, Double> groupingTagsWithLabels = new HashMap<>();

    private double lastUsedLabel = -1.0;

    private final double labelTagRange = 1.0;

    /**
     * Gets label.
     *
     * @param tag the tag
     * @return the label
     */
    public synchronized double getLabel(final Tag tag) {
        if (!groupingTagsWithLabels.containsKey(tag)) {
            lastUsedLabel += 1.0;
            groupingTagsWithLabels.put(tag, lastUsedLabel);
        }
        return groupingTagsWithLabels.get(tag);
    }

    /**
     * Tag from label tag.
     *
     * @param label the label
     * @return the tag
     */
    public Tag tagFromLabel(final Double label) {
        return groupingTagsWithLabels.entrySet()
                .stream()
                .filter(entry -> new DoubleRange(label - labelTagRange / 2, label + labelTagRange / 2).containsDouble(entry.getValue()))
                .findFirst()
                .orElseGet(handleMinMaxValue(label))
                .getKey();
    }

    private Supplier<Map.Entry<Tag, Double>> handleMinMaxValue(double label) {

        final Set<Map.Entry<Tag, Double>> tagsWithLabelsSet = groupingTagsWithLabels.entrySet();

        final Optional<Map.Entry<Tag, Double>> minValue = tagsWithLabelsSet.stream()
                .min((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()));

        final Optional<Map.Entry<Tag, Double>> maxValue = tagsWithLabelsSet.stream()
                .max((entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue()))
                .map(Function.identity());

        return label <= minValue.get().getValue() ? minValue::get : maxValue::get;

    }

}
