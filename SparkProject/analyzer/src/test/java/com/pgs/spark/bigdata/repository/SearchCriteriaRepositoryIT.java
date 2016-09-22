package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.domain.SearchProfile;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class SearchCriteriaRepositoryIT {

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Autowired
    private SearchCriteriaRepository searchCriteriaRepository;

    private SearchProfile searchProfile;

    @Before
    public void setUp() {
        searchProfile = SearchProfile.builder()
            .name(RandomStringUtils.randomAlphabetic(10))
            .build();
        searchProfileRepository.save(searchProfile);
    }

    @After
    public void tearDown() {
        searchProfileRepository.delete(searchProfile);
    }

    @Test
    public void shouldFindSearchCriteriaBySearchProfile() {
        //given
        final Set<SearchCriteria> referenceCriterias = buildSearchCriteria(10, searchProfile.getId());
        referenceCriterias.forEach(searchCriteriaRepository::save);

        //when
        final List<SearchCriteria> criterias = searchCriteriaRepository.findBySearchProfileId(searchProfile.getId());

        //then
        assertTrue(referenceCriterias.containsAll(criterias));
        assertTrue(criterias.containsAll(referenceCriterias));

        criterias.forEach(searchCriteriaRepository::delete);
    }

    private Set<SearchCriteria> buildSearchCriteria(final int count, final UUID searchProfileId) {
        Set<SearchCriteria> criterias = new HashSet<>();
        for (int i = 0; i < count; i++) {
            criterias.add(SearchCriteria.builder()
                .searchProfileId(searchProfileId)
                .searchProfileName(RandomStringUtils.randomAlphabetic(10))
                .keyWord(RandomStringUtils.randomAlphabetic(10))
                .mustHaveKeyword(RandomStringUtils.randomAlphabetic(10))
                .excludedWord(RandomStringUtils.randomAlphabetic(10))
                .build());
        }
        return criterias;
    }
}
