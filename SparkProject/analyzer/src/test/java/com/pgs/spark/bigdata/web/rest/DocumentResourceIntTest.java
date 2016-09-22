package com.pgs.spark.bigdata.web.rest;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.repository.DocumentRepository;
import com.pgs.spark.bigdata.service.DocumentService;
import com.pgs.spark.bigdata.web.rest.dto.DocumentDTO;
import com.pgs.spark.bigdata.web.rest.mapper.DocumentMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Test class for the DocumentResource REST controller.
 *
 * @see DocumentResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
public class DocumentResourceIntTest {

    private static final String DEFAULT_URL = "AAAAA";
    private static final String UPDATED_URL = "BBBBB";

    private static final String DEFAULT_CONTENT = "AAAAA";
    private static final String UPDATED_CONTENT = "BBBBB";

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_UPDATE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_UPDATE_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restDocumentMockMvc;

    private Document document;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DocumentResource documentResource = new DocumentResource();
        ReflectionTestUtils.setField(documentResource, "documentService", documentService);
        ReflectionTestUtils.setField(documentResource, "documentMapper", documentMapper);
        this.restDocumentMockMvc = MockMvcBuilders.standaloneSetup(documentResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        document = new Document();
        document.setUrl(DEFAULT_URL);
        document.setContent(DEFAULT_CONTENT);
        document.setCreationDate(DEFAULT_CREATION_DATE);
        document.setUpdateDate(DEFAULT_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void createDocument() throws Exception {
        final int databaseSizeBeforeCreate = documentRepository.findAll().size();

        // Create the Document
        final DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);

        final String contentAsString = restDocumentMockMvc.perform(post("/api/documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        final UUID id = TestUtil.convertStringToObject(contentAsString, DocumentDTO.class).getId();


        // Validate the Document in the database
        final List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeCreate + 1);
        final Document testDocument = documentRepository.findOne(id);
        assertThat(testDocument.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testDocument.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testDocument.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testDocument.getUpdateDate()).isEqualTo(DEFAULT_UPDATE_DATE);
    }

    @Test
    @Transactional
    public void checkUrlIsRequired() throws Exception {
        final int databaseSizeBeforeTest = documentRepository.findAll().size();
        // set the field null
        document.setUrl(null);

        // Create the Document, which fails.
        final DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);

        restDocumentMockMvc.perform(post("/api/documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isBadRequest());

        final List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getDocument() throws Exception {
        // Initialize the database
        documentRepository.save(document);

        // Get the document
        restDocumentMockMvc.perform(get("/api/documents/{id}", document.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(document.getId().toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.updateDate").value(DEFAULT_UPDATE_DATE.toString()));

        documentRepository.delete(document);
    }


    @Test
    @Transactional
    public void updateDocument() throws Exception {
        // Initialize the database
        documentRepository.save(document);
        final int databaseSizeBeforeUpdate = documentRepository.findAll().size();

        // Update the document
        final Document updatedDocument = new Document();
        updatedDocument.setId(document.getId());
        updatedDocument.setUrl(UPDATED_URL);
        updatedDocument.setContent(UPDATED_CONTENT);
        updatedDocument.setCreationDate(UPDATED_CREATION_DATE);
        updatedDocument.setUpdateDate(UPDATED_UPDATE_DATE);
        final DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(updatedDocument);

        final String contentAsString = restDocumentMockMvc.perform(put("/api/documents")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(documentDTO)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        final UUID id = TestUtil.convertStringToObject(contentAsString, DocumentDTO.class).getId();

        // Validate the Document in the database
        final List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeUpdate);
        final Document testDocument = documentRepository.findOne(id);
        assertThat(testDocument.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testDocument.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testDocument.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testDocument.getUpdateDate()).isEqualTo(UPDATED_UPDATE_DATE);

        documentRepository.delete(document);
    }

    @Test
    @Transactional
    public void deleteDocument() throws Exception {
        // Initialize the database
        documentRepository.save(document);
        final int databaseSizeBeforeDelete = documentRepository.findAll().size();

        // Get the document
        restDocumentMockMvc.perform(delete("/api/documents/{id}", document.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        final List<Document> documents = documentRepository.findAll();
        assertThat(documents).hasSize(databaseSizeBeforeDelete - 1);

        documentRepository.delete(document);
    }

    @Test
    @Transactional
    public void shouldShuffleDates() throws Exception {
        //given
        document.setUpdateDate(LocalDate.now().minus(1, ChronoUnit.DAYS));
        document = documentRepository.save(document);

        //when
        restDocumentMockMvc.perform(get("/api/documents/shuffleDates")
            .accept(TestUtil.APPLICATION_JSON_UTF8)
            .param("from", LocalDate.now().minus(3, ChronoUnit.CENTURIES).toString())
            .param("until", LocalDate.now().toString()))
            .andExpect(status().isOk());

        //then
        final Document testDocument = documentRepository.findOne(document.getId());
        assertThat(testDocument.getUpdateDate()).isNotEqualTo(document.getUpdateDate());

        documentRepository.delete(document);
    }

    @Test
    @Transactional
    public void shouldGetDocumentsByIds() throws Exception {
        //given
        documentRepository.save(document);

        //when
        JSONParser parser = new JSONParser();
        JSONObject response = (JSONObject) parser.parse(
            restDocumentMockMvc.perform(get("/api/documents/list")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .param("documentsIds", document.getId().toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        );

        //then
        assertNotNull(response.get(document.getId().toString()));
        assertTrue(document.getUpdateDate().toString().equals(((JSONObject)response.get(document.getId().toString())).get("updateDate")));
        assertTrue(document.getCreationDate().toString().equals(((JSONObject)response.get(document.getId().toString())).get("creationDate")));
        assertTrue(document.getUrl().equals(((JSONObject)response.get(document.getId().toString())).get("url")));
        assertTrue(document.getContent().equals(((JSONObject)response.get(document.getId().toString())).get("content")));

        documentRepository.delete(document);

    }

    @Test
    @Transactional
    public void getAllDocuments() throws Exception {
        //given
        final long defaultPageOfDocumentsSize = 20L;
        final long numberOfDocuments = documentRepository.count();
        long responseSize = defaultPageOfDocumentsSize < numberOfDocuments ? defaultPageOfDocumentsSize : numberOfDocuments;
        //when
        JSONParser parser = new JSONParser();
        JSONArray response = (JSONArray) parser.parse(
            restDocumentMockMvc.perform(get("/api/documents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        );

        //then
        assertNotNull(response);
        assertThat(Long.valueOf(response.size())).isEqualTo(responseSize);
    }
}
