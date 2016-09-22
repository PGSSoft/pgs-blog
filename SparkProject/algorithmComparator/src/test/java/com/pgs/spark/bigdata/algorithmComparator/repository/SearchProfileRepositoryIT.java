package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.pgs.spark.bigdata.algorithmComparator.Application;
import com.pgs.spark.bigdata.algorithmComparator.domain.SearchProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class SearchProfileRepositoryIT {

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Test
    public void shouldCreateSearchProfile() {
        //given
        final SearchProfile searchProfile = SearchProfile.builder()
                .name("searchProfile").build();

        final SearchProfile savedSearchProfile = searchProfileRepository.save(searchProfile);

        //when
        final SearchProfile testSearchProfile = searchProfileRepository.findOne(savedSearchProfile.getId());

        //then
        assertEquals(searchProfile.getId(), testSearchProfile.getId());
        searchProfileRepository.delete(searchProfile.getId());
    }
}
