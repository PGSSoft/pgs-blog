package com.pgs.spark.bigdata.web.rest;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.domain.SearchProfile;
import com.pgs.spark.bigdata.repository.SearchCriteriaRepository;
import com.pgs.spark.bigdata.repository.SearchProfileRepository;
import com.pgs.spark.bigdata.service.SearchCriteriaService;
import com.pgs.spark.bigdata.web.rest.dto.SearchCriteriaDTO;
import com.pgs.spark.bigdata.web.rest.mapper.SearchCriteriaMapper;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
 * Test class for the SearchCriteriaResource REST controller.
 *
 * @see SearchCriteriaResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class SearchCriteriaResourceIntTest {

    private static final String DEFAULT_KEY_WORD = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_KEY_WORD = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_MUST_HAVE_WORD = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_MUST_HAVE_WORD = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_EXCLUDED_WORD = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_EXCLUDED_WORD = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private SearchCriteriaRepository searchCriteriaRepository;

    @Inject
    private SearchCriteriaMapper searchCriteriaMapper;

    @Inject
    private SearchCriteriaService searchCriteriaService;

    @Inject
    private SearchProfileRepository searchProfileRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSearchCriteriaMockMvc;

    private SearchCriteria searchCriteria;

    private SearchProfile searchProfile;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SearchCriteriaResource searchCriteriaResource = new SearchCriteriaResource();
        ReflectionTestUtils.setField(searchCriteriaResource, "searchCriteriaService", searchCriteriaService);
//        ReflectionTestUtils.setField(searchCriteriaResource, "searchCriteriaMapper", searchCriteriaMapper);
        this.restSearchCriteriaMockMvc = MockMvcBuilders.standaloneSetup(searchCriteriaResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        searchProfile = new SearchProfile();
        searchProfile.setName("AAAAAAAAAAAAA");
        searchProfileRepository.save(searchProfile);
        searchCriteria = new SearchCriteria();
        searchCriteria.setKeyWord(DEFAULT_KEY_WORD);
        searchCriteria.setMustHaveWord(DEFAULT_MUST_HAVE_WORD);
        searchCriteria.setExcludedWord(DEFAULT_EXCLUDED_WORD);
        searchCriteria.setSearchProfileId(searchProfile.getId());
    }

    @After
    public void tearDown() {
        searchProfileRepository.delete(searchProfile.getId());
    }

    @Test
    @Transactional
    public void createSearchCriteria() throws Exception {
        final int databaseSizeBeforeCreate = searchCriteriaRepository.findAll().size();

        // Create the SearchCriteria
        final SearchCriteriaDTO searchCriteriaDTO = searchCriteriaMapper.searchCriteriaToSearchCriteriaDTO(searchCriteria);

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(
            restSearchCriteriaMockMvc.perform(post("/api/search-criteria")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(searchCriteriaDTO)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        // Validate the SearchCriteria in the database
        final List<SearchCriteria> searchCriteria = searchCriteriaRepository.findAll();
        assertThat(searchCriteria).hasSize(databaseSizeBeforeCreate + 1);
        final SearchCriteria testSearchCriteria = searchCriteriaRepository.findOne(UUID.fromString((String) response.get("id")));
        assertThat(testSearchCriteria.getKeyWord()).isEqualTo(DEFAULT_KEY_WORD);
        assertThat(testSearchCriteria.getMustHaveWord()).isEqualTo(DEFAULT_MUST_HAVE_WORD);
        assertThat(testSearchCriteria.getExcludedWord()).isEqualTo(DEFAULT_EXCLUDED_WORD);
        searchCriteriaRepository.delete(UUID.fromString((String) response.get("id")));
    }

    @Test
    @Transactional
    public void checkKeyWordIsRequired() throws Exception {
        final int databaseSizeBeforeTest = searchCriteriaRepository.findAll().size();
        // set the field null
        searchCriteria.setKeyWord(null);

        // Create the SearchCriteria, which fails.
        final SearchCriteriaDTO searchCriteriaDTO = searchCriteriaMapper.searchCriteriaToSearchCriteriaDTO(searchCriteria);

        restSearchCriteriaMockMvc.perform(post("/api/search-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(searchCriteriaDTO)))
            .andExpect(status().isBadRequest());

        final List<SearchCriteria> searchCriteria = searchCriteriaRepository.findAll();
        assertThat(searchCriteria).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getSearchCriteria() throws Exception {
        // Initialize the database
        searchCriteriaRepository.save(searchCriteria);

        // Get the searchCriteria
        restSearchCriteriaMockMvc.perform(get("/api/search-criteria/{id}", searchCriteria.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(searchCriteria.getId().toString()))
            .andExpect(jsonPath("$.keyWord").value(DEFAULT_KEY_WORD.toString()))
            .andExpect(jsonPath("$.mustHaveWord").value(DEFAULT_MUST_HAVE_WORD.toString()))
            .andExpect(jsonPath("$.excludedWord").value(DEFAULT_EXCLUDED_WORD.toString()));

        searchCriteriaRepository.delete(searchCriteria.getId());
    }

    @Test
    @Transactional
    public void getNonExistingSearchCriteria() throws Exception {
        // Get the searchCriteria
        restSearchCriteriaMockMvc.perform(get("/api/search-criteria/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSearchCriteria() throws Exception {
        // Initialize the database
        searchCriteriaRepository.save(searchCriteria);
        final int databaseSizeBeforeUpdate = searchCriteriaRepository.findAll().size();

        // Update the searchCriteria
        final SearchCriteria updatedSearchCriteria = new SearchCriteria();
        updatedSearchCriteria.setId(searchCriteria.getId());
        updatedSearchCriteria.setKeyWord(UPDATED_KEY_WORD);
        updatedSearchCriteria.setMustHaveWord(UPDATED_MUST_HAVE_WORD);
        updatedSearchCriteria.setExcludedWord(UPDATED_EXCLUDED_WORD);
        updatedSearchCriteria.setSearchProfileId(searchProfile.getId());
        final SearchCriteriaDTO searchCriteriaDTO = searchCriteriaMapper.searchCriteriaToSearchCriteriaDTO(updatedSearchCriteria);

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(restSearchCriteriaMockMvc.perform(put("/api/search-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(searchCriteriaDTO)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        // Validate the SearchCriteria in the database
        final List<SearchCriteria> searchCriteriaList = searchCriteriaRepository.findAll();
        assertThat(searchCriteriaList).hasSize(databaseSizeBeforeUpdate);
        final SearchCriteria testSearchCriteria = searchCriteriaRepository.findOne(UUID.fromString((String) response.get("id")));
        assertThat(testSearchCriteria.getKeyWord()).isEqualTo(UPDATED_KEY_WORD);
        assertThat(testSearchCriteria.getMustHaveWord()).isEqualTo(UPDATED_MUST_HAVE_WORD);
        assertThat(testSearchCriteria.getExcludedWord()).isEqualTo(UPDATED_EXCLUDED_WORD);

        searchCriteriaRepository.delete(searchCriteria.getId());
    }

    @Test
    @Transactional
    public void deleteSearchCriteria() throws Exception {
        // Initialize the database
        searchCriteriaRepository.save(searchCriteria);
        final int databaseSizeBeforeDelete = searchCriteriaRepository.findAll().size();

        // Get the searchCriteria
        restSearchCriteriaMockMvc.perform(delete("/api/search-criteria/{id}", searchCriteria.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        final List<SearchCriteria> searchCriteriaList = searchCriteriaRepository.findAll();
        assertThat(searchCriteriaList).hasSize(databaseSizeBeforeDelete - 1);
        searchCriteriaRepository.delete(searchCriteria.getId());
    }

    @Test
    @Transactional
    public void shouldGetAllSearchCriteria() throws Exception {
        //given
        searchCriteriaRepository.save(searchCriteria);
        final String searchProfileIds = searchCriteria.getSearchProfileId().toString();

        //when
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = (JSONArray)parser.parse(
            restSearchCriteriaMockMvc.perform(get("/api/search-criteria")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .param("searchProfileIds", searchProfileIds))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString()
        );

        //then
        assertTrue(jsonArray.stream().filter(o -> {
            JSONObject jsonObject = (JSONObject)o;
            return searchCriteria.getId().equals(UUID.fromString((String) jsonObject.get("id")));
        }).findAny().isPresent());

        searchCriteriaRepository.delete(searchCriteria);
    }
}
