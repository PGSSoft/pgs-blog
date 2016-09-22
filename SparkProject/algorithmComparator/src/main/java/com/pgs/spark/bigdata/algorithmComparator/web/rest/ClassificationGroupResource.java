package com.pgs.spark.bigdata.algorithmComparator.web.rest;

import com.pgs.spark.bigdata.algorithmComparator.dto.ChartDataDTO;
import com.pgs.spark.bigdata.algorithmComparator.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.algorithmComparator.service.ClassificationGroupService;
import com.pgs.spark.bigdata.algorithmComparator.web.rest.util.HeaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * The type Classification group resource.
 */
@RestController
@RequestMapping("/api/algorithmComparator")
public class ClassificationGroupResource {

    @Autowired
    private ClassificationGroupService classificationGroupService;

    /**
     * Perform comparison of algorithms using given search profile and training ratio.
     *
     * @param searchProfile the search profile
     * @param trainingRatio the training ratio
     * @return the response entity
     */
    @RequestMapping(value = "/performComparison",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> performComparison(@RequestParam(value = "searchProfile", required = true) String searchProfile,
                                                  @RequestParam(value = "trainingRatio", required = false) Integer trainingRatio) {
        classificationGroupService.performClassificationUsingAllAlgorithms(UUID.fromString(searchProfile), trainingRatio);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("Algorithm compare: ", "All")).build();
    }

    /**
     * Gets comparison data.
     *
     * @param searchProfile the search profile
     * @param scale         the scale
     * @param from          the from
     * @param until         the until
     * @return the comparison data
     */
    @RequestMapping(value = "/getComparisonData",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ChartDataDTO>> getComparisonData(@RequestParam(value = "searchProfile", required = true) String searchProfile,
                                                                       @RequestParam(value = "scale", required = true) ChartScaleDTO scale,
                                                                       @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                                       @RequestParam("until") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until) {
        final Map<String, ChartDataDTO> chartDataDTO = classificationGroupService.getChartData(UUID.fromString(searchProfile), scale, from, until);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("Algorithm compare: ", "All")).body(chartDataDTO);
    }
}
