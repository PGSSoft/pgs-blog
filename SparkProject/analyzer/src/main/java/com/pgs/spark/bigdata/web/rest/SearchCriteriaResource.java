package com.pgs.spark.bigdata.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.pgs.spark.bigdata.service.SearchCriteriaService;
import com.pgs.spark.bigdata.web.rest.dto.SearchCriteriaDTO;
import com.pgs.spark.bigdata.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing SearchCriteria.
 */
@RestController
@RequestMapping("/api")
public class SearchCriteriaResource {

    private final Logger log = LoggerFactory.getLogger(SearchCriteriaResource.class);

    @Inject
    private SearchCriteriaService searchCriteriaService;

    /**
     * POST  /search-criteria : Create a new searchCriteria.
     *
     * @param searchCriteriaDTO the searchCriteriaDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new searchCriteriaDTO, or with status 400 (Bad Request) if the searchCriteria has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/search-criteria",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SearchCriteriaDTO> createSearchCriteria(@Valid @RequestBody SearchCriteriaDTO searchCriteriaDTO) throws URISyntaxException {
        log.debug("REST request to save SearchCriteria : {}", searchCriteriaDTO);
        if (searchCriteriaDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("searchCriteria", "idexists", "A new searchCriteria cannot already have an ID")).body(null);
        }
        final SearchCriteriaDTO result = searchCriteriaService.save(searchCriteriaDTO);
        return ResponseEntity.created(new URI("/api/search-criteria/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("searchCriteria", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /search-criteria : Updates an existing searchCriteria.
     *
     * @param searchCriteriaDTO the searchCriteriaDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated searchCriteriaDTO,
     * or with status 400 (Bad Request) if the searchCriteriaDTO is not valid,
     * or with status 500 (Internal Server Error) if the searchCriteriaDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/search-criteria",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SearchCriteriaDTO> updateSearchCriteria(@Valid @RequestBody SearchCriteriaDTO searchCriteriaDTO) throws URISyntaxException {
        log.debug("REST request to update SearchCriteria : {}", searchCriteriaDTO);
        if (searchCriteriaDTO.getId() == null) {
            return createSearchCriteria(searchCriteriaDTO);
        }
        final SearchCriteriaDTO result = searchCriteriaService.save(searchCriteriaDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("searchCriteria", searchCriteriaDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /search-criteria : get all the searchCriteria.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of searchCriteria in body
     */
    @RequestMapping(value = "/search-criteria",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<SearchCriteriaDTO> getAllSearchCriteria(@RequestParam("searchProfileIds") List<String> searchProfileIds) {
        log.debug("REST request to get all SearchCriteria");
        return searchCriteriaService.findAll(searchProfileIds);
    }

    /**
     * GET  /search-criteria/:id : get the "id" searchCriteria.
     *
     * @param id the id of the searchCriteriaDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the searchCriteriaDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/search-criteria/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SearchCriteriaDTO> getSearchCriteria(@PathVariable UUID id) {
        log.debug("REST request to get SearchCriteria : {}", id);
        final SearchCriteriaDTO searchCriteriaDTO = searchCriteriaService.findOne(id);
        return Optional.ofNullable(searchCriteriaDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /search-criteria/:id : delete the "id" searchCriteria.
     *
     * @param id the id of the searchCriteriaDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/search-criteria/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSearchCriteria(@PathVariable UUID id) {
        log.debug("REST request to delete SearchCriteria : {}", id);
        searchCriteriaService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("searchCriteria", id.toString())).build();
    }

}
