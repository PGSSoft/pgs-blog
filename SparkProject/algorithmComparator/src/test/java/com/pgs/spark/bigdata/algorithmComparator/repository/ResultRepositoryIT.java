package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.pgs.spark.bigdata.algorithmComparator.Application;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import com.pgs.spark.bigdata.algorithmComparator.domain.Document;
import com.pgs.spark.bigdata.algorithmComparator.domain.Result;
import com.pgs.spark.bigdata.algorithmComparator.domain.SearchCriteria;
import com.pgs.spark.bigdata.algorithmComparator.domain.SearchProfile;
import com.pgs.spark.bigdata.algorithmComparator.util.PageRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResultRepositoryIT {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SearchCriteriaRepository searchCriteriaRepository;

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    private SearchProfile searchProfile;

    private SearchCriteria searchCriteria;

    private Document document;

    @Before
    public void setUp() {
        searchProfile = searchProfileRepository.save(
                SearchProfile.builder()
                        .name("searchProfile")
                        .build()
        );

        searchCriteria = searchCriteriaRepository.save(
                SearchCriteria.builder()
                        .searchProfileId(searchProfile.getId())
                        .excludedWord("excludedWord")
                        .keyWord("keyWord")
                        .mustHaveWord("mustHaveKeyword")
                        .searchProfileName("searchProfileName")
                        .build()
        );

        document = documentRepository.save(
                Document.builder()
                        .content("content")
                        .creationDate(LocalDate.now())
                        .updateDate(LocalDate.now())
                        .url("http://url.com")
                        .build()
        );
    }

    @After
    public void tearDown() {
        documentRepository.delete(document);
        searchCriteriaRepository.delete(searchCriteria);
        searchProfileRepository.delete(searchProfile);
    }

    @Test
    public void shouldFindByDocumentIdAndSearchProfileId() {
        //given
        final List<Result> resultList = buildResults(10, true);

        resultList.forEach(resultRepository::save);

        //when
        final List<Result> testingResults = resultRepository.findByDocumentIdAndSearchProfileId(document.getId(), searchProfile.getId());

        //then
        assertTrue(resultList.containsAll(testingResults));
        assertTrue(testingResults.containsAll(resultList));

        testingResults.forEach(resultRepository::delete);
    }

    @Test
    public void shouldFindBySearchProfileToTraining() {
        //given
        final List<Result> results = buildResults(10, true);

        results.forEach(resultRepository::save);

        //when
        final List<Result> testingResults = resultRepository.findBySearchProfileToTraining(searchProfile.getId());

        //then
        assertTrue(results.containsAll(testingResults));
        assertTrue(testingResults.containsAll(results));

        testingResults.forEach(resultRepository::delete);
    }

    @Test
    public void shouldFindBySearchProfileToEstimating() {
        //given
        final List<Result> results = buildResults(10, false);

        results.forEach(resultRepository::save);

        //when
        final List<Result> testingResults = resultRepository.findBySearchProfileToEstimating(searchProfile.getId(), new PageRequest(null, Integer.MAX_VALUE));

        //then
        assertTrue(results.containsAll(testingResults));
        assertTrue(testingResults.containsAll(results));

        testingResults.forEach(resultRepository::delete);
    }

    @Test
    public void shouldGetOneByResultId() {
        //given
        final Result buildResult = Result.unclassifiedResult(
                searchProfile.getId(),
                document.getId(),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                LocalDate.now());

        final Result result = resultRepository.save(buildResult);

        //when
        final Result testedResult = resultRepository.findOne(result.getId());

        //then
        assertEquals(result, testedResult);

        resultRepository.delete(testedResult);
    }

    private List<Result> buildResults(final int count, final boolean isTrainingData) {
        final List<Result> results = new ArrayList<>();
        final Random random = new Random();

        for (int i = 0; i < count; i++) {
            results.add(Result.builder()
                    .classification(Classification.values()[random.nextInt(Classification.values().length)])
                    .documentContent(RandomStringUtils.randomAlphabetic(10))
                    .documentDate(LocalDate.now())
                    .documentId(document.getId())
                    .documentUrl(RandomStringUtils.randomAlphabetic(10))
                    .isTrainingData(isTrainingData)
                    .searchProfileId(searchProfile.getId())
                    .build()
            );
        }
        return results;
    }
}
