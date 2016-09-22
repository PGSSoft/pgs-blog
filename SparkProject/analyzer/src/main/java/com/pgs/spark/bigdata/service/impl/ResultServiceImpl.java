package com.pgs.spark.bigdata.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.pgs.spark.bigdata.domain.User;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.security.SecurityUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.domain.enumeration.Classification;
import com.pgs.spark.bigdata.repository.ResultRepository;
import com.pgs.spark.bigdata.service.ResultService;
import com.pgs.spark.bigdata.web.rest.dto.ChartDataDTO;
import com.pgs.spark.bigdata.web.rest.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import com.pgs.spark.bigdata.web.rest.mapper.ResultMapper;

/**
 * Service Implementation for managing Result.
 */
@Service
@Transactional
public class ResultServiceImpl implements ResultService {

    private static final String DATE_SEPARATOR = "/";

    private final Logger log = LoggerFactory.getLogger(ResultServiceImpl.class);

    @Inject
    private ResultRepository resultRepository;

    @Inject
    private ResultMapper resultMapper;

    @Inject
    private UserRepository userRepository;

    /**
     * Save a result.
     *
     * @param resultDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ResultDTO save(ResultDTO resultDTO) {
        log.debug("Request to save Result : {}", resultDTO);
        Result result;
        if (resultDTO.getId() != null) {
            result = resultRepository.findOne(resultDTO.getId());
        } else {
            result = resultMapper.resultDTOToResult(resultDTO);
        }
        resultMapper.resultDTOToResult(resultDTO, result);
        result = resultRepository.save(result);
        final ResultDTO resultTO = resultMapper.resultToResultDTO(result);
        return resultTO;
    }

    /**
     * Get all the results.
     *
     * @param pageable the pagination information
     * @param searchProfileIds
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Result> findAll(Pageable pageable, List<String> searchProfileIds) {
        log.debug("Request to get all Results");
        final List<UUID> profileIds = searchProfileIds.stream().map(UUID::fromString).collect(Collectors.toList());
        final Page<Result> result = resultRepository.findAllPageable(pageable, profileIds);
        return result;
    }

    /**
     * Get one result by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public ResultDTO findOne(UUID id) {
        log.debug("Request to get Result : {}", id);
        final Result result = resultRepository.findOne(id);
        final ResultDTO resultDTO = resultMapper.resultToResultDTO(result);
        return resultDTO;
    }

    /**
     * Delete the  result by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Result : {}", id);
        resultRepository.delete(id);
    }

    /**
     * find results for given search Profile
     *
     * @param searchProfile searched profile for searched result;
     */
    @Override
    public List<ResultDTO> findBySearchProfile(UUID searchProfile) {
        final List<Result> results = resultRepository.findBySearchProfile(searchProfile);
        return resultMapper.resultsToResultDTOs(results);
    }

    @Override
    public ChartDataDTO prepareChartData(final UUID searchProfileId, final LocalDate from, final LocalDate until, final ChartScaleDTO scale) {
        final ChartDataDTO dto = new ChartDataDTO();
        final Map<Pair<Classification, String>, Long> numbers = new TreeMap<>();
        for (final Classification classification : Classification.values()) {
            resultRepository.findResultsBySearchProfileAndClassificationInRange(searchProfileId, classification, from, until)
                .stream()
                .map(getConverter(LocalDate.class, Long.class))
                .forEach(localDateLongPair -> numbers.merge(
                    Pair.of(classification, prepareLabel(scale, localDateLongPair.getLeft())),
                    localDateLongPair.getRight(),
                    (a, b) -> a + b));
        }
        dto.setLabels(numbers.keySet()
            .stream()
            .map(Pair::getRight)
            .collect(Collectors.toSet()).stream()
            .sorted(this::stringifiedDateComparator)
            .collect(Collectors.toList()));
        final List<String> labels = dto.getLabels();
        for (final Classification classification : dto.getSeries()) {
            final long[] dataSet = new long[labels.size()];
            numbers.keySet().stream()
                .filter(key -> classification.equals(key.getLeft()))
                .forEach(pair -> dataSet[labels.indexOf(pair.getRight())] += numbers.get(pair));
            dto.addDataSet(dataSet);
        }

        return dto;
    }

    private String prepareLabel(ChartScaleDTO scale, LocalDate localDate) {
        return DateTimeFormatter
            .ofPattern(scale.getPattern())
            .format(localDate);
    }

    private int stringifiedDateComparator(String a, String b) {
        final String[] aDate = a.split(DATE_SEPARATOR);
        final String[] bDate = b.split(DATE_SEPARATOR);

        for (int i = aDate.length - 1; i >= 0; i--) {
            final Integer aNum = Integer.parseInt(aDate[i]);
            final Integer bNum = Integer.parseInt(bDate[i]);

            final int compare = aNum.compareTo(bNum);
            if (compare != 0) {
                return compare;
            }
        }

        return 0;
    }

    private <L, R> Function<Object[], Pair<L, R>> getConverter(Class<L> left, Class<R> right) {
        return (objects) -> Pair.of(left.cast(objects[0]), right.cast(objects[1]));
    }
}
