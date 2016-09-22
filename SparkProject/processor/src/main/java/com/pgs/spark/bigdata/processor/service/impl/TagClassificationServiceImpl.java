package com.pgs.spark.bigdata.processor.service.impl;

import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.domain.Tag;
import com.pgs.spark.bigdata.processor.service.TagClassificationService;
import com.pgs.spark.bigdata.processor.utils.TagClassificationHolder;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.Content;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.LabeledContent;
import com.pgs.spark.bigdata.processor.repository.ResultRepository;
import com.pgs.spark.bigdata.processor.repository.TagRepository;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Tag classification service.
 */
@Service
public class TagClassificationServiceImpl implements TagClassificationService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private TagClassificationHolder tagClassificationHolder;

    @Autowired
    private SQLContext sqlContext;

    @Override
    public DataFrame getBySuperTags(final UUID searchProfileId) {
        final List<Tag> superTags = getSuperTags();
        return toLabeledDataFrame(superTags
                .stream()
                .collect(Collectors.toMap(Function.identity(), parentTag -> resultRepository.getResultsByParentTag(parentTag, searchProfileId))));
    }

    @Override
    public DataFrame getWithoutSuperTag(final UUID searchProfileId) {
        final List<Tag> tags = tagRepository.getWithoutSuperTag();
        return toDataFrame(tags
                .stream()
                .collect(Collectors.toMap(Function.identity(), tag -> resultRepository.getResultsByTag(tag, searchProfileId))));
    }

    private List<Tag> getSuperTags() {
        return tagRepository.getSuperTags();
    }

    private DataFrame toDataFrame(final Map<Tag, List<Result>> resultsByTags) {
        return sqlContext.createDataFrame(resultsByTags.entrySet()
                        .stream()
                        .flatMap(this::documentsToContent)
                        .collect(Collectors.toList()),
                Content.class);
    }

    private DataFrame toLabeledDataFrame(final Map<Tag, List<Result>> resultsByTags) {
        return sqlContext.createDataFrame(resultsByTags.entrySet()
                        .stream()
                        .flatMap(this::taggedDocumentsToLabeledContent)
                        .collect(Collectors.toList()),
                LabeledContent.class);
    }

    private Stream<Content> documentsToContent(Map.Entry<Tag, List<Result>> entry) {
        return entry.getValue()
                .stream()
                .map(result -> new Content(
                        result.getId().toString(),
                        result.getDocumentId().toString(),
                        result.getSearchProfileId().toString(),
                        result.getDocumentContent()
                ));
    }

    private Stream<LabeledContent> taggedDocumentsToLabeledContent(Map.Entry<Tag, List<Result>> entry) {
        return entry.getValue()
                .stream()
                .map(result -> new LabeledContent(
                        result.getId().toString(),
                        result.getDocumentId().toString(),
                        result.getSearchProfileId().toString(),
                        result.getDocumentContent(),
                        tagClassificationHolder.getLabel(entry.getKey())
                ));
    }
}
