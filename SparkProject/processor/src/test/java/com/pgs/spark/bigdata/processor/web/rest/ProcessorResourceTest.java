package com.pgs.spark.bigdata.processor.web.rest;

import com.google.common.collect.ImmutableMap;
import com.pgs.spark.bigdata.processor.jobs.SparkJob;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProcessorResourceTest {

    private MockMvc restMockMvc;

    private ProcessorResource processorResource;

    private Map<String, SparkJob> jobProvider;

    private Map<String, String> jobsDescriptions;

    @Before
    public void setUp(){
        processorResource = new ProcessorResource();
        restMockMvc = MockMvcBuilders.standaloneSetup(processorResource).build();

        jobProvider = ImmutableMap.of(
                "job0", mock(SparkJob.class),
                "job1", mock(SparkJob.class));
        jobsDescriptions = ImmutableMap.of("job0", "the job 0", "job1", "the job 1");

        ReflectionTestUtils.setField(processorResource, "jobProvider", jobProvider);
        ReflectionTestUtils.setField(processorResource, "jobsDescriptions", jobsDescriptions);
    }

    @Test
    public void shouldReturnPossibleJobs() throws Exception {
        //given
        JSONParser parser = new JSONParser();

        //when
        JSONObject response = (JSONObject) parser.parse(
            restMockMvc.perform(get("/api/processor/possibleJobs"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        );

        //then
        assertEquals(jobsDescriptions, response);
    }

    @Test
    public void shouldPerformJob() throws Exception {
        //given
        final String jobName = jobsDescriptions.keySet().iterator().next();
        final UUID searchProfileId = UUID.randomUUID();

        //when
        restMockMvc.perform(get("/api/processor/performJob")
                .param("jobName", jobName)
                .param("searchProfile", searchProfileId.toString()))
                .andExpect(status().isOk());

        //then
        verify(jobProvider.get(jobName)).run(searchProfileId);
    }

}
