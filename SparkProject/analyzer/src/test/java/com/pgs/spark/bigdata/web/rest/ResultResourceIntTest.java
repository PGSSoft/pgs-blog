package com.pgs.spark.bigdata.web.rest;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.domain.SearchProfile;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import com.pgs.spark.bigdata.repository.DocumentRepository;
import com.pgs.spark.bigdata.repository.ResultRepository;
import com.pgs.spark.bigdata.repository.SearchProfileRepository;
import com.pgs.spark.bigdata.service.ResultService;
import com.pgs.spark.bigdata.web.rest.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import com.pgs.spark.bigdata.web.rest.mapper.ResultMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ResultResource REST controller.
 *
 * @see ResultResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class ResultResourceIntTest {

    private static final Classification DEFAULT_CLASSIFICATION = Classification.POSITIVE;
    private static final Classification UPDATED_CLASSIFICATION = Classification.NEGATIVE;

    private static final Boolean DEFAULT_IS_TRAINING_DATA = false;
    private static final Boolean UPDATED_IS_TRAINING_DATA = true;

    @Inject
    private ResultRepository resultRepository;

    @Inject
    private SearchProfileRepository searchProfileRepository;

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private ResultMapper resultMapper;

    @Inject
    private ResultService resultService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restResultMockMvc;

    private Result result;

    private Document document;

    private SearchProfile searchProfile;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ResultResource resultResource = new ResultResource();
        ReflectionTestUtils.setField(resultResource, "resultService", resultService);
        ReflectionTestUtils.setField(resultResource, "resultMapper", resultMapper);
        this.restResultMockMvc = MockMvcBuilders.standaloneSetup(resultResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        searchProfile = new SearchProfile();
        searchProfile.setName("AAAAAAAAAAAAAAAAA");
        searchProfileRepository.save(searchProfile);
        document = documentRepository.findAllPageable(new PageRequest(0, 1)).getContent().get(0);
        result = new Result();
        result.setClassification(DEFAULT_CLASSIFICATION);
        result.setIsTrainingData(DEFAULT_IS_TRAINING_DATA);
        result.setSearchProfileId(searchProfile.getId());
        result.setDocumentId(document.getId());
    }

    @After
    public void tearDown() {
        searchProfileRepository.delete(searchProfile.getId());
    }

    @Test
    @Transactional
    public void createResult() throws Exception {
        final int databaseSizeBeforeCreate = resultRepository.findAll().size();

        // Create the Result
        final ResultDTO resultDTO = resultMapper.resultToResultDTO(result);

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(restResultMockMvc.perform(post("/api/results")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resultDTO)))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        // Validate the Result in the database
        final List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeCreate + 1);
        final Result testResult = resultRepository.findOne(UUID.fromString((String) response.get("id")));
        assertThat(testResult.getClassification()).isEqualTo(DEFAULT_CLASSIFICATION);
        assertThat(testResult.getIsTrainingData()).isEqualTo(DEFAULT_IS_TRAINING_DATA);
        resultRepository.delete(testResult);
    }

    @Test
    @Transactional
    public void getResult() throws Exception {
        // Initialize the database
        resultRepository.save(result);

        // Get the result
        restResultMockMvc.perform(get("/api/results/{id}", result.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(result.getId().toString()))
            .andExpect(jsonPath("$.classification").value(DEFAULT_CLASSIFICATION.toString()))
            .andExpect(jsonPath("$.isTrainingData").value(DEFAULT_IS_TRAINING_DATA));

        resultRepository.delete(result);
    }

    @Test
    @Transactional
    public void getNonExistingResult() throws Exception {
        // Get the result
        restResultMockMvc.perform(get("/api/results/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResult() throws Exception {
        // Initialize the database
        resultRepository.save(result);
        final int databaseSizeBeforeUpdate = resultRepository.findAll().size();

        // Update the result
        final Result updatedResult = new Result();
        updatedResult.setId(result.getId());
        updatedResult.setDocumentId(document.getId());
        updatedResult.setSearchProfileId(searchProfile.getId());
        updatedResult.setClassification(UPDATED_CLASSIFICATION);
        updatedResult.setIsTrainingData(UPDATED_IS_TRAINING_DATA);
        final ResultDTO resultDTO = resultMapper.resultToResultDTO(updatedResult);

        restResultMockMvc.perform(put("/api/results")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(resultDTO)))
            .andExpect(status().isOk());

        // Validate the Result in the database
        final List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeUpdate);
        final Result testResult = resultRepository.findOne(result.getId());
        assertThat(testResult.getClassification()).isEqualTo(UPDATED_CLASSIFICATION);
        assertThat(testResult.getIsTrainingData()).isEqualTo(UPDATED_IS_TRAINING_DATA);

        resultRepository.delete(result);
    }

    @Test
    @Transactional
    public void deleteResult() throws Exception {
        // Initialize the database
        resultRepository.save(result);
        final int databaseSizeBeforeDelete = resultRepository.findAll().size();

        // Get the result
        restResultMockMvc.perform(delete("/api/results/{id}", result.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        final List<Result> results = resultRepository.findAll();
        assertThat(results).hasSize(databaseSizeBeforeDelete - 1);
        resultRepository.delete(result);
    }

    @Test
    @Transactional
    public void getAllResults() throws Exception {
        //given
        final Result testingResult = resultRepository.save(result);
        final String searchProfileIds = testingResult.getSearchProfileId().toString();
        //when
        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(
            restResultMockMvc.perform(get("/api/results")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .param("searchProfileIds", searchProfileIds))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        );

        //then
        assertTrue(response.stream().filter(o -> {
            JSONObject jsonObject = (JSONObject)o;
            return testingResult.getId().equals(UUID.fromString((String) jsonObject.get("id")));
        }).findAny().isPresent());

        resultRepository.delete(result);
    }

    @Test
    @Transactional
    public void shouldGetChartData() throws Exception {
        //given
        final UUID searchProfileId = searchProfile.getId();
        LocalDate from = LocalDate.now().minus(10, ChronoUnit.MONTHS);
        LocalDate until = LocalDate.now();
        ChartScaleDTO chartScaleDTO = ChartScaleDTO.MONTHS;

        //when
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(
            restResultMockMvc.perform(get("/api/results/chartData")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .param("searchProfile", searchProfileId.toString())
                .param("from", from.toString())
                .param("until", until.toString())
                .param("scale", chartScaleDTO.toString()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        );

        //then
        assertNotNull(response.get("data"));
        assertNotNull(response.get("series"));
        assertNotNull(response.get("labels"));
    }
}
