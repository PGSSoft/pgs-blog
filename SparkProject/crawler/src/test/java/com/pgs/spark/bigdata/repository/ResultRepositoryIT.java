package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.Application;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.domain.SearchProfile;
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

import static org.junit.Assert.assertEquals;

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
    public void shouldGetOneByResultId() {
        //given
        final Result buildResult = buildResult(true);
        final Result result = resultRepository.save(buildResult);

        //when
        final Result testedResult = resultRepository.findOne(result.getId());

        //then
        assertEquals(result, testedResult);
    }

    private Result buildResult(final boolean isTrainingData) {
        return Result.builder()
                .documentContent(RandomStringUtils.randomAlphabetic(10))
                .documentDate(LocalDate.now())
                .documentId(document.getId())
                .documentUrl(RandomStringUtils.randomAlphabetic(10))
                .isTrainingData(isTrainingData)
                .searchProfileId(searchProfile.getId())
                .build();
    }

}
