package com.pgs.spark.bigdata.algorithmComparator.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pgs.spark.bigdata.algorithmComparator.domain.Algorithm;
import com.pgs.spark.bigdata.algorithmComparator.domain.AlgorithmAccuracy;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import com.pgs.spark.bigdata.algorithmComparator.dto.ChartDataDTO;
import com.pgs.spark.bigdata.algorithmComparator.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.algorithmComparator.repository.AlgorithmAccuracyRepository;
import com.pgs.spark.bigdata.algorithmComparator.repository.ClassificationGroupRepository;
import com.pgs.spark.bigdata.algorithmComparator.service.impl.ClassificationGroupServiceImpl;
import com.pgs.spark.bigdata.algorithmComparator.web.jms.JmsService;

public class ClassificationGroupServiceTest {

    @InjectMocks
    private final ClassificationGroupService classificationGroupService = new ClassificationGroupServiceImpl();

    @Mock
    private ClassificationGroupRepository classificationGroupRepository;

    @Mock
    private JmsService jmsService;

    @Mock
    private AlgorithmAccuracyRepository algorithmAccuracyRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPerformClassificationUsingAllAlgorithms() {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final Integer trainingRatio = 80;

        //when
        classificationGroupService.performClassificationUsingAllAlgorithms(searchProfileId, trainingRatio);

        //then
        for (final Algorithm algorithm : Algorithm.values()) {
            verify(jmsService).sendAlgorithmProcessingRequest(algorithm, searchProfileId, Optional.of(trainingRatio));
        }
    }

    @Test
    public void shouldGetChartData() {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final ChartScaleDTO scale = ChartScaleDTO.MONTHS;
        final LocalDate from = LocalDate.MIN;
        final LocalDate to = LocalDate.MAX;
        final AlgorithmAccuracy algorithmAccuracy = new AlgorithmAccuracy();
        algorithmAccuracy.setAccuracy(80);
        algorithmAccuracy.setName("name");
        algorithmAccuracy.setId(UUID.randomUUID());

        final List<Object[]> crossPositive = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> crossNegative = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> simplePositive = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> simpleNegative = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> trainPositive = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> trainNegative = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> multilayerPositive = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final List<Object[]> multilayerNegative = Arrays.asList(
                new Object[]{LocalDate.of(2014, 6, 1), 10L},
                new Object[]{LocalDate.of(2014, 7, 2), 20L},
                new Object[]{LocalDate.of(2015, 1, 3), 50L},
                new Object[]{LocalDate.now(), 20L}
        );

        final Map<String, List<List<Long>>> dataForGivenAlgorithm = new HashMap<>();

        dataForGivenAlgorithm.put(Algorithm.CROSS_VALIDATION.name(), Arrays.asList(
                crossPositive.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList()),
                crossNegative.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList())));

        dataForGivenAlgorithm.put(Algorithm.SIMPLE.name(), Arrays.asList(
                simplePositive.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList()),
                simpleNegative.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList())));

        dataForGivenAlgorithm.put(Algorithm.TRAIN_VALIDATION.name(), Arrays.asList(
                trainPositive.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList()),
                trainNegative.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList())));

        dataForGivenAlgorithm.put(Algorithm.MULTILAYER_PERCEPTRON.name(), Arrays.asList(
                multilayerPositive.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList()),
                multilayerNegative.stream().map(pair -> (Long) pair[1]).collect(Collectors.toList())));

        final List<String> algorithms = new ArrayList<>();
        for (final Algorithm algorithm : Algorithm.values()) {
            algorithms.add(algorithm.name());
        }

        when(algorithmAccuracyRepository.getByAlgorithm(any(), eq(searchProfileId))).thenReturn(Optional.of(algorithmAccuracy));
        when(classificationGroupRepository.findResultsBySearchProfileAndCrossClassificationInRange(searchProfileId, Classification.POSITIVE, from, to))
                .thenReturn(crossPositive);

        when(classificationGroupRepository.findResultsBySearchProfileAndCrossClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to))
                .thenReturn(crossNegative);

        when(classificationGroupRepository.findResultsBySearchProfileAndMultilayerClassificationInRange(searchProfileId, Classification.POSITIVE, from, to))
                .thenReturn(multilayerPositive);

        when(classificationGroupRepository.findResultsBySearchProfileAndMultilayerClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to))
                .thenReturn(multilayerNegative);

        when(classificationGroupRepository.findResultsBySearchProfileAndSimpleClassificationInRange(searchProfileId, Classification.POSITIVE, from, to))
                .thenReturn(simplePositive);

        when(classificationGroupRepository.findResultsBySearchProfileAndSimpleClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to))
                .thenReturn(simpleNegative);

        when(classificationGroupRepository.findResultsBySearchProfileAndTrainClassificationInRange(searchProfileId, Classification.POSITIVE, from, to))
                .thenReturn(trainPositive);

        when(classificationGroupRepository.findResultsBySearchProfileAndTrainClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to))
                .thenReturn(trainNegative);

        //when
        final Map<String, ChartDataDTO> chartData = classificationGroupService.getChartData(searchProfileId, scale, from, to);

        //then
        verify(algorithmAccuracyRepository, times(Algorithm.values().length)).getByAlgorithm(any(), eq(searchProfileId));
        verify(classificationGroupRepository).findResultsBySearchProfileAndCrossClassificationInRange(searchProfileId, Classification.POSITIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndCrossClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndMultilayerClassificationInRange(searchProfileId, Classification.POSITIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndMultilayerClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndSimpleClassificationInRange(searchProfileId, Classification.POSITIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndSimpleClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndTrainClassificationInRange(searchProfileId, Classification.POSITIVE, from, to);
        verify(classificationGroupRepository).findResultsBySearchProfileAndTrainClassificationInRange(searchProfileId, Classification.NEGATIVE, from, to);

        assertTrue(chartData.keySet().containsAll(algorithms));

        for (final String algorithm : algorithms) {
            assertEquals(chartData.get(algorithm).getAccuracy(), algorithmAccuracy.getAccuracy());
            assertTrue(chartData.get(algorithm).getSeries().containsAll(Arrays.asList(Classification.values())));
            assertEquals(chartData.get(algorithm).getData().size(), Classification.values().length);
            assertEquals(chartData.get(algorithm).getData(), dataForGivenAlgorithm.get(algorithm));
        }
    }
}
