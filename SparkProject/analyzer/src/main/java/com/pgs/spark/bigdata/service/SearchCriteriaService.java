package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.web.rest.dto.SearchCriteriaDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing SearchCriteria.
 */

public interface SearchCriteriaService {

    /**
     * Save a searchCriteria.
     *
     * @param searchCriteriaDTO the entity to save
     * @return the persisted entity
     */
    SearchCriteriaDTO save(SearchCriteriaDTO searchCriteriaDTO);

    /**
     * Get all the searchCriteria.
     *
     * @return the list of entities
     * @param searchProfileIds
     */
    List<SearchCriteriaDTO> findAll(List<String> searchProfileIds);

    /**
     * Get one searchCriteria by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    SearchCriteriaDTO findOne(UUID id);

    /**
     * Delete the  searchCriteria by id.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);
}
