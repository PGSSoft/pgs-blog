package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.Application;
import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.domain.SearchProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SearchCriteriaRepositoryIT {

    @Autowired
    private SearchCriteriaRepository searchCriteriaRepository;

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    private SearchProfile searchProfile;

    private SearchCriteria searchCriteria;

    @Before
    public void setUp() {
        searchProfile = new SearchProfile();
        searchProfile.setName("searchProfile");
        searchProfile = searchProfileRepository.save(searchProfile);

        searchCriteria = new SearchCriteria();
        searchCriteria.setSearchProfileName(searchProfile.getName());
        searchCriteria.setSearchProfileId(searchProfile.getId());
        searchCriteria.setKeyWord("keyWord");
        searchCriteria.setMustHaveWord("mustHaveWord");
        searchCriteria.setExcludedWord("excludedWord");
        searchCriteriaRepository.save(searchCriteria);
    }

    @After
    public void tearDown() {
        searchCriteriaRepository.delete(searchCriteria);
        searchProfileRepository.delete(searchProfile);
    }

    @Test
    public void shouldBeTrue() {
        //given
        final UUID searchProfileId = searchProfile.getId();

        //when
        final List<SearchCriteria> searchCriteriaList = searchCriteriaRepository.findAllBySearchProfileId(searchProfileId);

        assertTrue(searchCriteriaList.contains(searchCriteria));
    }
}
