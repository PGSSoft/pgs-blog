package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.domain.enumeration.Classification;
import com.pgs.spark.bigdata.repository.ResultRepository;
import com.pgs.spark.bigdata.service.impl.ResultServiceImpl;
import com.pgs.spark.bigdata.web.rest.dto.ChartDataDTO;
import com.pgs.spark.bigdata.web.rest.dto.ChartScaleDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResultServiceImplTest {

    @InjectMocks
    private ResultService resultService = new ResultServiceImpl();

    @Mock
    private ResultRepository resultRepository;

    @Test
    public void shouldPrepareChartData() {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final Classification classification = Classification.POSITIVE;
        final LocalDate from = LocalDate.now().minus(10, ChronoUnit.DAYS);
        final LocalDate to = LocalDate.now();
        final LocalDate firstDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
        final LocalDate secondDate = LocalDate.now();
        final LocalDate thirdDate = LocalDate.now().plus(1, ChronoUnit.MONTHS);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        when(resultRepository.findResultsBySearchProfileAndClassificationInRange(searchProfileId, classification, from, to))
            .thenReturn(Arrays.asList(
                new Object[]{firstDate, 10L},
                new Object[]{secondDate, 15L},
                new Object[]{thirdDate, 20L}
            ));

        //when
        final ChartDataDTO chartDataDTO = resultService.prepareChartData(searchProfileId, from, to, ChartScaleDTO.MONTHS);

        //then
        assertEquals(Arrays.asList(Classification.values()), chartDataDTO.getSeries());
        assertEquals(formatter.format(firstDate), chartDataDTO.getLabels().get(0));
        assertEquals(formatter.format(secondDate), chartDataDTO.getLabels().get(1));
        assertEquals(formatter.format(thirdDate), chartDataDTO.getLabels().get(2));
        assertEquals(new Long(10), chartDataDTO.getData().get(0).get(0));
        assertEquals(new Long(15), chartDataDTO.getData().get(0).get(1));
        assertEquals(new Long(20), chartDataDTO.getData().get(0).get(2));
        for (int i = 1; i < chartDataDTO.getData().size(); i++) {
            assertTrue(chartDataDTO.getData().get(i).size() == 3);
            for (int j = 0; j < chartDataDTO.getData().get(i).size(); j++) {
                assertTrue(chartDataDTO.getData().get(i).get(j) == 0);
            }
        }
    }
}
