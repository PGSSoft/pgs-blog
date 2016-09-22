package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.AnalyzerApp;
import com.pgs.spark.bigdata.domain.SearchProfile;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import com.pgs.spark.bigdata.repository.SearchCriteriaRepository;
import com.pgs.spark.bigdata.repository.SearchProfileRepository;
import com.pgs.spark.bigdata.web.rest.dto.DocumentDTO;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import com.pgs.spark.bigdata.web.rest.dto.SearchCriteriaDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnalyzerApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class SearchCriteriaServiceIntTest {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SearchProfileRepository searchProfileRepository;

    @Autowired
    private SearchCriteriaRepository searchCriteriaRepository;

    @Autowired
    private SearchCriteriaService searchCriteriaService;

    @Autowired
    private ResultService resultService;

    @Test
    public void testUpdateSearchCriteria() {
        SearchProfile profile = new SearchProfile();
        profile.setName("testProfile");
        profile = searchProfileRepository.save(profile);
        List<String> searchProfileIds = Collections.singletonList(profile.getId().toString());
        SearchCriteriaDTO searchCriteriaDTO = new SearchCriteriaDTO();
        searchCriteriaDTO.setKeyWord("test");
        searchCriteriaDTO.setSearchProfileId(profile.getId());
        searchCriteriaDTO = searchCriteriaService.save(searchCriteriaDTO);

        final DocumentDTO document = new DocumentDTO();
        document.setContent("test content");
        document.setUrl("test url");
        final DocumentDTO save = documentService.save(document);

        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setClassification(Classification.POSITIVE);
        resultDTO.setDocumentId(save.getId());
        resultDTO.setSearchProfileId(profile.getId());
        resultDTO = resultService.save(resultDTO);

        assertThat(resultService.findAll(new PageRequest(0, 10), searchProfileIds).getSize()).isNotEqualTo(0);

        searchCriteriaDTO.setMustHaveWord("must have test word");
        searchCriteriaDTO = searchCriteriaService.save(searchCriteriaDTO);

        assertThat(resultService.findAll(new PageRequest(0, 10), searchProfileIds).getSize()).isNotEqualTo(0);

        SearchProfile profile2 = new SearchProfile();
        profile2.setName("testProfile2");
        profile2 = searchProfileRepository.save(profile2);
        SearchCriteriaDTO searchCriteriaDTO2 = new SearchCriteriaDTO();
        searchCriteriaDTO2.setKeyWord("test2");
        searchCriteriaDTO2.setSearchProfileId(profile2.getId());
        searchCriteriaDTO2 = searchCriteriaService.save(searchCriteriaDTO2);

        final DocumentDTO documentDTO2 = new DocumentDTO();
        documentDTO2.setContent("test 2");
        documentDTO2.setUrl("test url2");

        ResultDTO resultDTO2 = new ResultDTO();
        resultDTO2.setClassification(Classification.POSITIVE);
        resultDTO2.setDocumentId(save.getId());
        resultDTO2.setSearchProfileId(profile2.getId());
        resultDTO2 = resultService.save(resultDTO2);

        assertThat(resultService.findAll(new PageRequest(0, 10), searchProfileIds)).isNotEqualTo(0);

        searchCriteriaDTO.setMustHaveWord("must have test word test");
        searchCriteriaService.save(searchCriteriaDTO);

        assertThat(resultService.findAll(new PageRequest(0, 10), searchProfileIds).getSize()).isNotEqualTo(0);

        searchCriteriaRepository.delete(searchCriteriaDTO.getId());
        searchCriteriaRepository.delete(searchCriteriaDTO2.getId());
        searchProfileRepository.delete(profile.getId());
        searchProfileRepository.delete(profile2.getId());
        resultService.delete(resultDTO.getId());
        resultService.delete(resultDTO2.getId());
    }
}
