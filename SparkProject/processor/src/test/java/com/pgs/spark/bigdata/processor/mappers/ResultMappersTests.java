package com.pgs.spark.bigdata.processor.mappers;

import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.Content;
import com.pgs.spark.bigdata.processor.jobs.sparkUtil.LabeledContent;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static com.pgs.spark.bigdata.processor.domain.Classification.POSITIVE;
import static org.junit.Assert.assertEquals;

public class ResultMappersTests {

    private static final UUID TEST_UUID = UUID.randomUUID();

    private static final UUID TEST_DOCUMENT_UUID = UUID.randomUUID();

    private static final UUID TEST_SEARCH_PROFILE_UUID = UUID.randomUUID();

    private static final String TEST_CONTENT = RandomStringUtils.random(1000);

    private ResultMappers resultMappers;

    private Result result;

    @Before
    public void before() {
        resultMappers = new ResultMappers();

        result = new Result();
        result.setId(TEST_UUID);
        result.setDocumentId(TEST_DOCUMENT_UUID);
        result.setSearchProfileId(TEST_SEARCH_PROFILE_UUID);
        result.setDocumentContent(TEST_CONTENT);
    }

    @Test
    public void toContentTest() {
        final Content content = resultMappers.toContent(result);

        assertEquals(TEST_UUID, UUID.fromString(content.getId()));
        assertEquals(TEST_CONTENT, content.getText());
    }

    @Test
    public void toLabeledContentTest() {
        result.setClassification(POSITIVE);
        final LabeledContent content = resultMappers.toLabeledContent(result);

        assertEquals(TEST_UUID, UUID.fromString(content.getId()));
        assertEquals(TEST_CONTENT, content.getText());
        assertEquals(POSITIVE.getSparkLabel(), content.getLabel(), 0.0);
    }
}
