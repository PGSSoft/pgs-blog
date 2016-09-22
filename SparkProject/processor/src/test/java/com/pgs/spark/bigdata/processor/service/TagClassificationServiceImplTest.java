package com.pgs.spark.bigdata.processor.service;

import com.pgs.spark.bigdata.processor.ProcessorApplication;
import com.pgs.spark.bigdata.processor.domain.Classification;
import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.domain.Tag;
import com.pgs.spark.bigdata.processor.service.impl.TagClassificationServiceImpl;
import com.pgs.spark.bigdata.processor.utils.TagClassificationHolder;
import com.pgs.spark.bigdata.processor.repository.ResultRepository;
import com.pgs.spark.bigdata.processor.repository.TagRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProcessorApplication.class)
@WebAppConfiguration
public class TagClassificationServiceImplTest {

    @InjectMocks
    private TagClassificationServiceImpl tagClassificationService = new TagClassificationServiceImpl();

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ResultRepository resultRepository;

    private final TagClassificationHolder tagClassificationHolder = new TagClassificationHolder();

    @Autowired
    private SQLContext sqlContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(tagClassificationService, "sqlContext", sqlContext);
        ReflectionTestUtils.setField(tagClassificationService, "tagClassificationHolder", tagClassificationHolder);
    }

    @Test
    public void shouldCreateDataFrameFromSearchProfileId() {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final UUID resultId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();

        final List<Tag> exampleTags = buildExampleParentTags(documentId, resultId, 10);
        final Map<Tag, List<Result>> exampleResultsByParentTag = exampleTags.stream()
                .collect(Collectors
                        .toMap(Function.identity(), parentTag -> buildExampleResults(documentId, resultId, 20))
                );
        final List<Result> results = exampleResultsByParentTag.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        final Map<Result, Double> resultsWithTagsAsLabels = convertToResultsWithTagsAsNumbers(exampleResultsByParentTag);

        when(tagRepository.getSuperTags()).thenReturn(exampleTags);

        when(resultRepository.getResultsByParentTag(any(Tag.class), any(UUID.class)))
                .thenAnswer(invocationOnMock -> {
                    final Tag tag = (Tag) invocationOnMock.getArguments()[0];
                    return exampleResultsByParentTag.get(tag);
                });
        //when
        final DataFrame bySuperTags = tagClassificationService.getBySuperTags(searchProfileId);

        //then
        bySuperTags.collectAsList().stream().forEach(row -> {
            final Result result = results.stream()
                    .filter(resultPredicate ->
                            resultPredicate.getId().equals(UUID.fromString(row.getString(1))) &&
                                    resultPredicate.getDocumentId().equals(UUID.fromString(row.getString(0)))
                    )
                    .findFirst().get();
            assertNotNull(result);
            assertNotNull(resultsWithTagsAsLabels.get(result));
        });
    }

    private List<Tag> buildExampleParentTags(final UUID documentId, final UUID resultId, int numberOfTags) {
        final List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < numberOfTags; i++) {
            tags.add(Tag.builder()
                    .documentId(documentId)
                    .resultId(resultId)
                    .isSuperTag(true)
                    .content(RandomStringUtils.randomAlphabetic(10))
                    .build()
            );
        }
        return tags;
    }

    private List<Result> buildExampleResults(final UUID documentId, final UUID searchProfileId, int numberOfResults) {
        final List<Result> results = new ArrayList<>();
        final Random random = new Random();
        for (int i = 0; i < numberOfResults; i++) {
            results.add(Result.builder()
                    .id(UUID.randomUUID())
                    .documentId(documentId)
                    .searchProfileId(searchProfileId)
                    .isTrainingData(true)
                    .documentUrl(RandomStringUtils.randomAlphabetic(10))
                    .documentContent(RandomStringUtils.randomAlphabetic(10))
                    .documentDate(LocalDate.now())
                    .classification(Classification.values()[random.nextInt(Classification.values().length)])
                    .build()
            );
        }
        return results;
    }

    private Map<Result, Double> convertToResultsWithTagsAsNumbers(final Map<Tag, List<Result>> tagListMap) {
        final TagClassificationHolder independentClassifier = new TagClassificationHolder();
        final Map<Result, Tag> resultTagMap = convertToReversedFlattenedMap(tagListMap);
        return resultTagMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> independentClassifier.getLabel(entry.getValue())));
    }

    @SuppressWarnings("unchecked")
    private Map<Result, Tag> convertToReversedFlattenedMap(final Map<Tag, List<Result>> map) {
        final Map<Result, Tag> resultMap = new HashMap<>();
        for (Map.Entry entry : map.entrySet()) {
            for (Result result : (List<Result>) entry.getValue()) {
                resultMap.put(result, (Tag) entry.getKey());
            }
        }
        return resultMap;
    }
}
