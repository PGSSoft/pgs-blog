package com.pgs.spark.bigdata.processor.repository;

import com.pgs.spark.bigdata.processor.ProcessorApplication;
import com.pgs.spark.bigdata.processor.domain.SearchCriteria;
import com.pgs.spark.bigdata.processor.domain.SearchProfile;
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
@SpringApplicationConfiguration(classes = ProcessorApplication.class)
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
    public void shouldFindBySearchProfileId() {
        //given
        final UUID searchProfileId = searchProfile.getId();

        //when
        final List<SearchCriteria> searchCriteriaList = searchCriteriaRepository.findBySearchProfileId(searchProfileId);

        assertTrue(searchCriteriaList.contains(searchCriteria));
    }
}
