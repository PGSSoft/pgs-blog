package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.web.rest.dto.SearchProfileDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service Interface for managing SearchProfile.
 */
public interface SearchProfileService {

    /**
     * Save a searchProfile.
     *
     * @param searchProfileDTO the entity to save
     * @return the persisted entity
     */
    SearchProfileDTO save(SearchProfileDTO searchProfileDTO);

    /**
     * Get all the searchProfiles.
     *
     * @return the list of entities
     */
    List<SearchProfileDTO> findAll();

    /**
     * Get the "id" searchProfile.
     *
     * @param id the id of the entity
     * @return the entity
     */
    SearchProfileDTO findOne(UUID id);

    /**
     * Delete the "id" searchProfile.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);
}
