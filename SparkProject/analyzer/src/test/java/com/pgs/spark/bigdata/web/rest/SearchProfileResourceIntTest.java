package com.pgs.spark.bigdata.web.rest;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.Authority;
import com.pgs.spark.bigdata.domain.SearchProfile;
import com.pgs.spark.bigdata.domain.User;
import com.pgs.spark.bigdata.repository.SearchProfileRepository;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.service.SearchProfileService;
import com.pgs.spark.bigdata.web.rest.dto.SearchProfileDTO;
import com.pgs.spark.bigdata.web.rest.mapper.SearchProfileMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
 * Test class for the SearchProfileResource REST controller.
 *
 * @see SearchProfileResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class SearchProfileResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private SearchProfileRepository searchProfileRepository;

    @Inject
    private SearchProfileMapper searchProfileMapper;

    @Inject
    private SearchProfileService searchProfileService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSearchProfileMockMvc;

    private SearchProfile searchProfile;

    TestingAuthenticationToken testingAuthenticationToken;

    User user;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        user = userRepository.findOneByLogin("admin").get();
        testingAuthenticationToken = new TestingAuthenticationToken(getUserDetails(user), null);
        SecurityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
        final SearchProfileResource searchProfileResource = new SearchProfileResource();
        ReflectionTestUtils.setField(searchProfileResource, "searchProfileService", searchProfileService);
        this.restSearchProfileMockMvc = MockMvcBuilders.standaloneSetup(searchProfileResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        searchProfile = new SearchProfile();
        searchProfile.setName(DEFAULT_NAME);
        searchProfile.setUserId(user.getId());
    }

    @Test
    @Transactional
    public void createSearchProfile() throws Exception {
        final int databaseSizeBeforeCreate = searchProfileRepository.findAll().size();

        // Create the SearchProfile
        final SearchProfileDTO searchProfileDTO = searchProfileMapper.searchProfileToSearchProfileDTO(searchProfile);

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(
            restSearchProfileMockMvc.perform(post("/api/search-profiles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(searchProfileDTO)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

        // Validate the SearchProfile in the database
        final List<SearchProfile> searchProfiles = searchProfileRepository.findAll();
        assertThat(searchProfiles).hasSize(databaseSizeBeforeCreate + 1);
        final SearchProfile testSearchProfile = searchProfileRepository.findOne(UUID.fromString((String) response.get("id")));
        assertThat(testSearchProfile.getName()).isEqualTo(DEFAULT_NAME);
        searchProfileRepository.delete(UUID.fromString((String) response.get("id")));
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        final int databaseSizeBeforeTest = searchProfileRepository.findAll().size();
        // set the field null
        searchProfile.setName(null);

        // Create the SearchProfile, which fails.
        final SearchProfileDTO searchProfileDTO = searchProfileMapper.searchProfileToSearchProfileDTO(searchProfile);

        restSearchProfileMockMvc.perform(post("/api/search-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(searchProfileDTO)))
            .andExpect(status().isBadRequest());

        final List<SearchProfile> searchProfiles = searchProfileRepository.findAll();
        assertThat(searchProfiles).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getSearchProfile() throws Exception {
        // Initialize the database
        searchProfileRepository.save(searchProfile);

        // Get the searchProfile
        restSearchProfileMockMvc.perform(get("/api/search-profiles/{id}", searchProfile.getId())
        .principal(testingAuthenticationToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(searchProfile.getId().toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));

        searchProfileRepository.delete(searchProfile.getId());
    }

    @Test
    @Transactional
    public void getNonExistingSearchProfile() throws Exception {
        // Get the searchProfile
        restSearchProfileMockMvc.perform(get("/api/search-profiles/{id}", UUID.randomUUID()).principal(testingAuthenticationToken))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void getAllSearchProfiles() throws Exception {
        //given
        final SearchProfile testingSearchProfile = searchProfileRepository.save(searchProfile);

        //when
        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(
        restSearchProfileMockMvc.perform(get("/api/search-profiles")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
        );

        //then
        assertTrue(response.stream().filter(o -> {
            JSONObject jsonObject = (JSONObject)o;
            return testingSearchProfile.getId().equals(UUID.fromString((String) jsonObject.get("id")));
        }).findAny().isPresent());

        searchProfileRepository.delete(searchProfile);
    }

    @Test
    @Transactional
    public void updateSearchProfile() throws Exception {
        // Initialize the database
        searchProfileRepository.save(searchProfile);
        final int databaseSizeBeforeUpdate = searchProfileRepository.findAll().size();

        // Update the searchProfile
        final SearchProfile updatedSearchProfile = new SearchProfile();
        updatedSearchProfile.setId(searchProfile.getId());
        updatedSearchProfile.setName(UPDATED_NAME);
        final SearchProfileDTO searchProfileDTO = searchProfileMapper.searchProfileToSearchProfileDTO(updatedSearchProfile);

        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(
            restSearchProfileMockMvc.perform(put("/api/search-profiles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(searchProfileDTO)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        // Validate the SearchProfile in the database
        final List<SearchProfile> searchProfiles = searchProfileRepository.findAll();
        assertThat(searchProfiles).hasSize(databaseSizeBeforeUpdate);
        final SearchProfile testSearchProfile = searchProfileRepository.findOne(UUID.fromString((String) response.get("id")));
        assertThat(testSearchProfile.getName()).isEqualTo(UPDATED_NAME);

        searchProfileRepository.delete(searchProfile.getId());
    }

    @Test
    @Transactional
    public void deleteSearchProfile() throws Exception {
        // Initialize the database
        searchProfileRepository.save(searchProfile);
        final int databaseSizeBeforeDelete = searchProfileRepository.findAll().size();

        // Get the searchProfile
        restSearchProfileMockMvc.perform(delete("/api/search-profiles/{id}", searchProfile.getId()).principal(testingAuthenticationToken)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        final List<SearchProfile> searchProfiles = searchProfileRepository.findAll();
        assertThat(searchProfiles).hasSize(databaseSizeBeforeDelete - 1);

        searchProfileRepository.delete(searchProfile);
    }

    private UserDetails getUserDetails(final User user){
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getName())).collect(Collectors.toList());
            }

            @Override
            public String getPassword() {
                return user.getPassword();
            }

            @Override
            public String getUsername() {
                return user.getLogin();
            }

            @Override
            public boolean isAccountNonExpired() {
                return false;
            }

            @Override
            public boolean isAccountNonLocked() {
                return false;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return false;
            }

            @Override
            public boolean isEnabled() {
                return false;
            }
        };
    }
}
