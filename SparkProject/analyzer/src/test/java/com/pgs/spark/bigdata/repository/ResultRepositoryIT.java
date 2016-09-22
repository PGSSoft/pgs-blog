package com.pgs.spark.bigdata.repository;

import com.google.common.collect.ImmutableMap;
import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class ResultRepositoryIT {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private Document testDocument;

    @Before
    public void setUp() {
        testDocument = Document.builder()
            .content(RandomStringUtils.randomAlphabetic(10))
            .creationDate(LocalDate.of(3000, 12, 12))
            .url(RandomStringUtils.randomAlphabetic(10))
            .build();

        testDocument = documentRepository.save(testDocument);
    }

    @After
    public void tearDown() {
        documentRepository.delete(testDocument);
    }

    @Test
    public void shouldFindResultsBySearchProfileAndClassificationInGivenRange() {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final Classification classification = Classification.POSITIVE;
        final LocalDate referenceDate = LocalDate.of(3000, 12, 12);
        final LocalDate from = referenceDate.minus(10, ChronoUnit.MONTHS);
        final LocalDate to = referenceDate.plus(10, ChronoUnit.MONTHS);
        final LocalDate testDate0 = to.minus(9, ChronoUnit.MONTHS);
        final LocalDate testDate1 = to.minus(6, ChronoUnit.MONTHS);
        final LocalDate testDate2 = to.minus(3, ChronoUnit.MONTHS);

        Result result0 = Result.bulder()
            .searchProfileId(searchProfileId)
            .classification(classification)
            .documentDate(testDate0)
            .documentId(testDocument.getId())
            .build();
        Result result1 = Result.bulder()
            .searchProfileId(searchProfileId)
            .classification(classification)
            .documentDate(testDate1)
            .documentId(testDocument.getId())
            .build();
        Result result2 = Result.bulder()
            .searchProfileId(searchProfileId)
            .classification(classification)
            .documentDate(testDate2)
            .documentId(testDocument.getId())
            .build();

        resultRepository.save(result0);
        resultRepository.save(result1);
        resultRepository.save(result2);

        //when
        final List<Object[]> groupedResults = resultRepository.findResultsBySearchProfileAndClassificationInRange(searchProfileId, classification, from, to);

        //then
        assertTrue(
            groupedResults.stream().map(objects -> objects[0]).collect(Collectors.toSet())
                .containsAll(Arrays.asList(testDate0, testDate1, testDate2))
        );

        for (Object[] groupedResult : groupedResults) {
            assertTrue(groupedResult[1].equals(1L));
        }

        resultRepository.delete(result0);
        resultRepository.delete(result1);
        resultRepository.delete(result2);

    }

    @Test
    public void shouldConvertToChartObject() {
        //given
        final List<LocalDate> dates = new ArrayList<>();
        final ZoneId zoneId = ZoneId.systemDefault();
        for(int i = 0 ; i < 10 ; i++){
            dates.add(LocalDate.now().minus(i*100, ChronoUnit.DAYS));
        }
        final Map<com.datastax.driver.core.LocalDate, Integer> results = new HashMap<>();

        dates.forEach(date -> results
            .put(com.datastax.driver.core.LocalDate.fromMillisSinceEpoch(date.atStartOfDay(zoneId).toEpochSecond()),
                RandomUtils.nextInt(10, 100)
            )
        );

        results.entrySet().stream().forEach(localDateIntegerEntry -> {

            //when
            Object[] chartObject = resultRepository.toChartObject(localDateIntegerEntry);

            //then
            final long daysSinceEpoch = localDateIntegerEntry.getKey().getDaysSinceEpoch();
            final LocalDate date = LocalDate.ofEpochDay(daysSinceEpoch);
            final long amount = localDateIntegerEntry.getValue().longValue();
            assertEquals(chartObject[0], date);
            assertEquals(chartObject[1], amount);

        });


    }
}
