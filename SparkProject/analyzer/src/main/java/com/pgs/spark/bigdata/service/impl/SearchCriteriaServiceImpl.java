package com.pgs.spark.bigdata.service.impl;

import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.repository.SearchCriteriaRepository;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.service.ResultService;
import com.pgs.spark.bigdata.service.SearchCriteriaService;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import com.pgs.spark.bigdata.web.rest.dto.SearchCriteriaDTO;
import com.pgs.spark.bigdata.web.rest.mapper.SearchCriteriaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchCriteriaServiceImpl implements SearchCriteriaService {

    private final Logger log = LoggerFactory.getLogger(SearchCriteriaServiceImpl.class);

    @Inject
    private SearchCriteriaRepository searchCriteriaRepository;

    @Inject
    private SearchCriteriaMapper searchCriteriaMapper;

    @Inject
    private ResultService resultService;

    @Inject
    private UserRepository userRepository;

    public SearchCriteriaDTO save(SearchCriteriaDTO searchCriteriaDTO) {
        log.debug("Request to save SearchCriteria : {}", searchCriteriaDTO);
        SearchCriteria searchCriteria = searchCriteriaMapper.searchCriteriaDTOToSearchCriteria(searchCriteriaDTO);
        cleanResults(searchCriteria);
        searchCriteria = searchCriteriaRepository.save(searchCriteria);
        final SearchCriteriaDTO result = searchCriteriaMapper.searchCriteriaToSearchCriteriaDTO(searchCriteria);
        return result;
    }

    private void cleanResults(SearchCriteria searchCriteria) {
        final List<ResultDTO> resultDTOs = resultService.findBySearchProfile(searchCriteria.getSearchProfileId());
        resultDTOs.forEach(r -> resultService.delete(r.getId()));
    }

    @Transactional(readOnly = true)
    public List<SearchCriteriaDTO> findAll(List<String> searchProfileIds) {
        log.debug("Request to get all SearchCriteria");
        final List<UUID> uuids = searchProfileIds.stream().map(UUID::fromString).collect(Collectors.toList());
        final List<SearchCriteriaDTO> result = searchCriteriaRepository.findAll(uuids).stream()
            .map(searchCriteriaMapper::searchCriteriaToSearchCriteriaDTO)
            .collect(Collectors.toCollection(LinkedList::new));
        return result;
    }

    @Transactional(readOnly = true)
    public SearchCriteriaDTO findOne(UUID id) {
        log.debug("Request to get SearchCriteria : {}", id);
        final SearchCriteria searchCriteria = searchCriteriaRepository.findOne(id);
        final SearchCriteriaDTO searchCriteriaDTO = searchCriteriaMapper.searchCriteriaToSearchCriteriaDTO(searchCriteria);
        return searchCriteriaDTO;
    }

    public void delete(UUID id) {
        log.debug("Request to delete SearchCriteria : {}", id);
        searchCriteriaRepository.delete(id);
    }
}
