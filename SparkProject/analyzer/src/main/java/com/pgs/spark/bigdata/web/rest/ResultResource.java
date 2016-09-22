package com.pgs.spark.bigdata.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.service.ResultService;
import com.pgs.spark.bigdata.web.rest.dto.ChartDataDTO;
import com.pgs.spark.bigdata.web.rest.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import com.pgs.spark.bigdata.web.rest.mapper.ResultMapper;
import com.pgs.spark.bigdata.web.rest.util.HeaderUtil;
import com.pgs.spark.bigdata.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * REST controller for managing Result.
 */
@RestController
@RequestMapping("/api/results")
public class ResultResource {

    private final Logger log = LoggerFactory.getLogger(ResultResource.class);

    @Inject
    private ResultService resultService;

    @Inject
    private ResultMapper resultMapper;

    /**
     * POST  /results : Create a new result.
     *
     * @param resultDTO the resultDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new resultDTO, or with status 400 (Bad Request) if the result has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResultDTO> createResult(@RequestBody ResultDTO resultDTO) throws URISyntaxException {
        log.debug("REST request to save Result : {}", resultDTO);
        if (resultDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("result", "idexists", "A new result cannot already have an ID")).body(null);
        }
        final ResultDTO result = resultService.save(resultDTO);
        return ResponseEntity.created(new URI("/api/results/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("result", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /results : Updates an existing result.
     *
     * @param resultDTO the resultDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated resultDTO,
     * or with status 400 (Bad Request) if the resultDTO is not valid,
     * or with status 500 (Internal Server Error) if the resultDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResultDTO> updateResult(@RequestBody ResultDTO resultDTO) throws URISyntaxException {
        log.debug("REST request to update Result : {}", resultDTO);
        if (resultDTO.getId() == null) {
            return createResult(resultDTO);
        }
        final ResultDTO result = resultService.save(resultDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("result", resultDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /results : get all the results.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of results in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<ResultDTO>> getAllResults(Pageable pageable, @RequestParam("searchProfileIds")  List<String> searchProfileIds)
        throws URISyntaxException {
        log.debug("REST request to get a page of Results");
        final Page<Result> page = resultService.findAll(pageable, searchProfileIds);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/results");
        return new ResponseEntity<>(resultMapper.resultsToResultDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /results/:id : get the "id" result.
     *
     * @param id the id of the resultDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the resultDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResultDTO> getResult(@PathVariable UUID id) {
        log.debug("REST request to get Result : {}", id);
        final ResultDTO resultDTO = resultService.findOne(id);
        return Optional.ofNullable(resultDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /results/:id : delete the "id" result.
     *
     * @param id the id of the resultDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteResult(@PathVariable UUID id) {
        log.debug("REST request to delete Result : {}", id);
        resultService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("result", id.toString())).build();
    }

    @RequestMapping(value = "/chartData",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ChartDataDTO> getChartData(@RequestParam("searchProfile") UUID searchProfile,
                                                     @RequestParam("from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
                                                     @RequestParam("until") @DateTimeFormat(iso = ISO.DATE) LocalDate until,
                                                     @RequestParam("scale") ChartScaleDTO scale) {
        log.debug("REST request to get chart data for search profile: {}, in time-range from {} to {} scaled to {}",
            searchProfile, from, until, scale.getDescription());
        return new ResponseEntity<>(resultService.prepareChartData(searchProfile, from, until, scale), HttpStatus.OK);
    }

}
