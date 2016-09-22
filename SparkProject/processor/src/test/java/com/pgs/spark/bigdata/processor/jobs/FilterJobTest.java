package com.pgs.spark.bigdata.processor.jobs;

import com.pgs.spark.bigdata.processor.ProcessorApplication;
import com.pgs.spark.bigdata.processor.domain.SearchCriteria;
import com.pgs.spark.bigdata.processor.jobs.filterJobs.FilterJob;
import com.pgs.spark.bigdata.processor.repository.ResultRepository;
import com.pgs.spark.bigdata.processor.repository.SearchCriteriaRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProcessorApplication.class)
@WebAppConfiguration
@PropertySource("classpath:application.properties")
public class FilterJobTest {

    @InjectMocks
    private SparkJob sparkJob = new FilterJob();

    @Autowired
    private SQLContext sqlContext;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private SearchCriteriaRepository searchCriteriaRepository;

    private final String keyspace = "analyzer";

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(sparkJob, "sqlContext", sqlContext);
        ReflectionTestUtils.setField(sparkJob, "keyspace", keyspace);
    }

    @Test
    public void shouldFilterResultsBasedOnSearchCriteria(){
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final List<SearchCriteria> criterias = buildSearchCriteria(searchProfileId, 3);
        when(searchCriteriaRepository.findBySearchProfileId(searchProfileId)).thenReturn(criterias);

        //when
        sparkJob.run(searchProfileId);

        //then no exception

    }

    private List<SearchCriteria> buildSearchCriteria(final UUID searchProfileId, final int count){
        final List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        for(int i = 0 ; i < count ; i++){
            searchCriteriaList.add(
                    SearchCriteria.builder()
                    .mustHaveWord(RandomStringUtils.randomAlphabetic(1))
                    .searchProfileId(searchProfileId)
                    .build()
            );
        }
        return searchCriteriaList;
    }

}
