package com.pgs.spark.bigdata.processor.web.jms;

import com.google.common.collect.ImmutableMap;
import com.pgs.spark.bigdata.processor.domain.Algorithm;
import com.pgs.spark.bigdata.processor.dto.AlgorithmEstimationDTO;
import com.pgs.spark.bigdata.processor.jobs.SparkJob;
import com.pgs.spark.bigdata.processor.web.jms.util.DtoProperties;
import com.pgs.spark.bigdata.processor.web.jms.util.Queues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.util.ReflectionTestUtils;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JmsServiceTest {

    @InjectMocks
    private JmsService jmsService = new JmsService();

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private SparkJob sparkJob;

    @Before
    public void setUp() {
        final Map<Algorithm, SparkJob> jobsMap = ImmutableMap.of(Algorithm.CROSS_VALIDATION, sparkJob);
        ReflectionTestUtils.setField(jmsService, "jobs", jobsMap);
    }

    @Test
    public void shouldOnReceiveRequest() throws JMSException {
        //given
        final ObjectMessage objectMessage = mock(ObjectMessage.class);
        final int trainingRatio = 80;
        final int estimationAccuracy = 70;
        final UUID searchProfileId = UUID.randomUUID();
        final Map<UUID, Double> resultEstimationMap = buildResultPredictionMap();
        final AlgorithmEstimationDTO estimationDTO = buildExampleEstimation(resultEstimationMap, trainingRatio, estimationAccuracy);
        when(objectMessage.getIntProperty(DtoProperties.TRAINING_RATIO)).thenReturn(trainingRatio);
        when(objectMessage.getStringProperty(DtoProperties.ALGORITHM)).thenReturn(Algorithm.CROSS_VALIDATION.getNormalizedName());
        when(objectMessage.getStringProperty(DtoProperties.SEARCH_PROFILE)).thenReturn(searchProfileId.toString());
        when(sparkJob.getPredictions(searchProfileId)).thenReturn(CompletableFuture.completedFuture(Optional.of(estimationDTO)));
        when(sparkJob.withTrainingRatio(anyInt())).thenCallRealMethod();

        //when
        jmsService.receiveRequest(objectMessage);

        //then
        final ArgumentCaptor<MessageCreator> creatorArgumentCaptor = ArgumentCaptor.forClass(MessageCreator.class);
        final ArgumentCaptor<String> resultQueueArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(jmsTemplate).send(resultQueueArgumentCaptor.capture(), creatorArgumentCaptor.capture());
        assertEquals(Queues.PROCESSING_RESULTS, resultQueueArgumentCaptor.getValue());
        assertEquals(Algorithm.CROSS_VALIDATION, ReflectionTestUtils.getField(creatorArgumentCaptor.getValue(), "arg$1"));
        assertEquals(estimationAccuracy, ReflectionTestUtils.getField(creatorArgumentCaptor.getValue(), "arg$2"));
        assertEquals(resultEstimationMap, ReflectionTestUtils.getField(creatorArgumentCaptor.getValue(), "arg$3"));
    }

    private AlgorithmEstimationDTO buildExampleEstimation(final Map<UUID, Double> resultEstimationMap, final int trainingRatio, final int estimationAccuracy) {
        return AlgorithmEstimationDTO.builder()
                .estimationAccuracy(estimationAccuracy)
                .trainingRatio(trainingRatio)
                .resultPredictionMap(resultEstimationMap)
                .build();
    }

    private Map<UUID, Double> buildResultPredictionMap() {
        return ImmutableMap.of(
                UUID.randomUUID(), 1.0,
                UUID.randomUUID(), 2.0,
                UUID.randomUUID(), 1.0,
                UUID.randomUUID(), 0.1,
                UUID.randomUUID(), 1.3
        );
    }
}
