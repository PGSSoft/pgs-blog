package com.pgs.spark.bigdata.algorithmComparator.web.rest;

import com.pgs.spark.bigdata.algorithmComparator.dto.ChartScaleDTO;
import com.pgs.spark.bigdata.algorithmComparator.service.ClassificationGroupService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ClassificationGroupResourceTest {

    private MockMvc mockMvc;

    @Mock
    private ClassificationGroupService classificationGroupService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ClassificationGroupResource classificationGroupResource = new ClassificationGroupResource();
        ReflectionTestUtils.setField(classificationGroupResource, "classificationGroupService", classificationGroupService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(classificationGroupResource).build();
    }

    @Test
    public void shouldPerformComparison() throws Exception {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final Integer trainingRatio = 90;

        //when
        mockMvc.perform(get("/api/algorithmComparator/performComparison")
                .param("searchProfile", searchProfileId.toString())
                .param("trainingRatio", trainingRatio.toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        //then
        verify(classificationGroupService).performClassificationUsingAllAlgorithms(searchProfileId, trainingRatio);
    }

    @Test
    public void shouldGetComparisonData() throws Exception {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final ChartScaleDTO chartScaleDTO = ChartScaleDTO.MONTHS;
        final LocalDate from = LocalDate.now().minus(10, ChronoUnit.DAYS);
        final LocalDate until = LocalDate.now();

        //when
        mockMvc.perform(get("/api/algorithmComparator/getComparisonData")
        .param("searchProfile", searchProfileId.toString())
        .param("scale", chartScaleDTO.toString())
        .param("from", from.toString())
        .param("until", until.toString())
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(classificationGroupService).getChartData(searchProfileId, chartScaleDTO, from, until);
    }
}
