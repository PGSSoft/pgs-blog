package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.web.rest.dto.ChartDataDTO;
import com.pgs.spark.bigdata.web.rest.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service Interface for managing Result.
 */
public interface ResultService {

    /**
     * Save a result.
     *
     * @param resultDTO the entity to save
     * @return the persisted entity
     */
    ResultDTO save(ResultDTO resultDTO);

    /**
     * Get all the results.
     *
     * @param pageable the pagination information
     * @param searchProfileIds
     * @return the list of entities
     */
    Page<Result> findAll(Pageable pageable, List<String> searchProfileIds);

    /**
     * Get the "id" result.
     *
     * @param id the id of the entity
     * @return the entity
     */
    ResultDTO findOne(UUID id);

    /**
     * Delete the "id" result.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * find results for given search Profile
     *
     * @param searchProfile searched profile for searched result;
     */
    List<ResultDTO> findBySearchProfile(UUID searchProfile);

    ChartDataDTO prepareChartData(final UUID searchProfile, final LocalDate from, final LocalDate until, final ChartScaleDTO scale);
}
