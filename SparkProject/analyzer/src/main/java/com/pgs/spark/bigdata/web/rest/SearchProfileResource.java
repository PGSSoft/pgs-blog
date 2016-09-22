package com.pgs.spark.bigdata.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.pgs.spark.bigdata.service.SearchProfileService;
import com.pgs.spark.bigdata.web.rest.dto.SearchProfileDTO;
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
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing SearchProfile.
 */
@RestController
@RequestMapping("/api")
public class SearchProfileResource {

    private final Logger log = LoggerFactory.getLogger(SearchProfileResource.class);

    @Inject
    private SearchProfileService searchProfileService;

    /**
     * POST  /search-profiles : Create a new searchProfile.
     *
     * @param searchProfileDTO the searchProfileDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new searchProfileDTO, or with status 400 (Bad Request) if the searchProfile has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/search-profiles",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SearchProfileDTO> createSearchProfile(@Valid @RequestBody SearchProfileDTO searchProfileDTO) throws URISyntaxException {
        log.debug("REST request to save SearchProfile : {}", searchProfileDTO);
        if (searchProfileDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("searchProfile", "idexists", "A new searchProfile cannot already have an ID")).body(null);
        }
        final SearchProfileDTO result = searchProfileService.save(searchProfileDTO);
        return ResponseEntity.created(new URI("/api/search-profiles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("searchProfile", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /search-profiles : Updates an existing searchProfile.
     *
     * @param searchProfileDTO the searchProfileDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated searchProfileDTO,
     * or with status 400 (Bad Request) if the searchProfileDTO is not valid,
     * or with status 500 (Internal Server Error) if the searchProfileDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/search-profiles",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SearchProfileDTO> updateSearchProfile(@Valid @RequestBody SearchProfileDTO searchProfileDTO) throws URISyntaxException {
        log.debug("REST request to update SearchProfile : {}", searchProfileDTO);
        if (searchProfileDTO.getId() == null) {
            return createSearchProfile(searchProfileDTO);
        }
        final SearchProfileDTO result = searchProfileService.save(searchProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("searchProfile", searchProfileDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /search-profiles : get all the searchProfiles.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of searchProfiles in body
     */
    @RequestMapping(value = "/search-profiles",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public List<SearchProfileDTO> getAllSearchProfiles() {
        log.debug("REST request to get all SearchProfiles");
        return searchProfileService.findAll();
    }

    /**
     * GET  /search-profiles/:id : get the "id" searchProfile.
     *
     * @param id the id of the searchProfileDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the searchProfileDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/search-profiles/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SearchProfileDTO> getSearchProfile(@PathVariable UUID id) {
        log.debug("REST request to get SearchProfile : {}", id);
        final SearchProfileDTO searchProfileDTO = searchProfileService.findOne(id);
        return Optional.ofNullable(searchProfileDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /search-profiles/:id : delete the "id" searchProfile.
     *
     * @param id the id of the searchProfileDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/search-profiles/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSearchProfile(@PathVariable UUID id) {
        log.debug("REST request to delete SearchProfile : {}", id);
        searchProfileService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("searchProfile", id.toString())).build();
    }

}
