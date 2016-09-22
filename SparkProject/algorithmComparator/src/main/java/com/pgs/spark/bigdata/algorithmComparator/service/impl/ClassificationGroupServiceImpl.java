package com.pgs.spark.bigdata.algorithmComparator.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import com.pgs.spark.bigdata.algorithmComparator.domain.AlgorithmAccuracy;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pgs.spark.bigdata.algorithmComparator.domain.Algorithm;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import com.pgs.spark.bigdata.algorithmComparator.dto.ChartDataDTO;
import com.pgs.spark.bigdata.algorithmComparator.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.algorithmComparator.repository.AlgorithmAccuracyRepository;
import com.pgs.spark.bigdata.algorithmComparator.repository.ClassificationGroupRepository;
import com.pgs.spark.bigdata.algorithmComparator.service.ClassificationGroupService;
import com.pgs.spark.bigdata.algorithmComparator.web.jms.JmsService;

/**
 * The type Classification group service.
 */
@Service
public class ClassificationGroupServiceImpl implements ClassificationGroupService {

    private static final String DATE_SEPARATOR = "/";

    @Autowired
    private ClassificationGroupRepository classificationGroupRepository;

    @Autowired
    private JmsService jmsService;

    @Autowired
    private AlgorithmAccuracyRepository algorithmAccuracyRepository;

    @Override
    public void performClassificationUsingAllAlgorithms(final UUID searchProfileId, final Integer trainingRatio) {
        algorithmAccuracyRepository.deleteBySearchProfileId(searchProfileId);
        classificationGroupRepository.deleteBySearchProfileId(searchProfileId);
        for (final Algorithm algorithm : Algorithm.values()) {
            jmsService.sendAlgorithmProcessingRequest(algorithm, searchProfileId, Optional.ofNullable(trainingRatio));
        }
    }

    @Override
    public Map<String, ChartDataDTO> getChartData(UUID searchProfile, ChartScaleDTO scale, LocalDate from, LocalDate until) {
        final Map<String, ChartDataDTO> algorithmData = new HashMap<>();
        for (final Algorithm algorithm : Algorithm.values()) {
            final ChartDataDTO data = prepareDataForAlgorithm(searchProfile, scale, from, until, algorithm);
            algorithmData.put(algorithm.name(), data);
        }
        return algorithmData;
    }

    private ChartDataDTO prepareDataForAlgorithm(final UUID searchProfile, final ChartScaleDTO scale, final LocalDate from, final LocalDate until, final Algorithm algorithm) {
        final ChartDataDTO dto = new ChartDataDTO();
        final Map<Pair<Classification, String>, Long> numbers = new TreeMap<>();
        for (final Classification classification : Classification.values()) {
            getClassificationGroupsByAlgorithm(algorithm, searchProfile, classification, from, until)
                    .stream()
                    .map((objects) -> Pair.of((LocalDate) objects[0], (Long) objects[1]))
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
            numbers.keySet()
                    .stream()
                    .filter(key -> classification.equals(key.getLeft()))
                    .forEach(pair -> dataSet[labels.indexOf(pair.getRight())] += numbers.get(pair));
            dto.addDataSet(dataSet);
        }

        Optional<AlgorithmAccuracy> algorithmAccuracy = algorithmAccuracyRepository.getByAlgorithm(algorithm, searchProfile);
        int accuracy = algorithmAccuracy.isPresent() ? algorithmAccuracy.get().getAccuracy() : 0;
        dto.setAccuracy(accuracy);
        return dto;
    }
    
    private String prepareLabel(ChartScaleDTO scale, LocalDate date) {
    	return DateTimeFormatter
    			.ofPattern(scale.getPattern())
    			.format(date);
    }

    private List<Object[]> getClassificationGroupsByAlgorithm(Algorithm algorithm, UUID searchProfile, Classification classification, LocalDate from, LocalDate until) {
        switch (algorithm) {
            case SIMPLE:
                return classificationGroupRepository.findResultsBySearchProfileAndSimpleClassificationInRange(searchProfile, classification, from, until);
            case CROSS_VALIDATION:
                return classificationGroupRepository.findResultsBySearchProfileAndCrossClassificationInRange(searchProfile, classification, from, until);
            case TRAIN_VALIDATION:
                return classificationGroupRepository.findResultsBySearchProfileAndTrainClassificationInRange(searchProfile, classification, from, until);
            case MULTILAYER_PERCEPTRON:
                return classificationGroupRepository.findResultsBySearchProfileAndMultilayerClassificationInRange(searchProfile, classification, from, until);
        }
        return Collections.emptyList();
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
}
