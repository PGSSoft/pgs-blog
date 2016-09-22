package com.pgs.spark.bigdata.algorithmComparator.web.jms;

import com.google.common.collect.ImmutableMap;
import com.pgs.spark.bigdata.algorithmComparator.domain.Algorithm;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import com.pgs.spark.bigdata.algorithmComparator.domain.ClassificationGroup;
import com.pgs.spark.bigdata.algorithmComparator.repository.AlgorithmAccuracyRepository;
import com.pgs.spark.bigdata.algorithmComparator.repository.ClassificationGroupRepository;
import com.pgs.spark.bigdata.algorithmComparator.repository.ResultRepository;
//import com.pgs.spark.bigdata.algorithmComparator.service.JobsTracker;
import com.pgs.spark.bigdata.algorithmComparator.web.jms.util.DtoProperties;
import com.pgs.spark.bigdata.algorithmComparator.web.jms.util.Queues;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JmsServiceTest {

    @InjectMocks
    private JmsService jmsService = new JmsService();

//    @Mock
//    private JobsTracker jobsTracker = mock(JobsTracker.class);

    @Mock
    private JmsTemplate jmsTemplate = mock(JmsTemplate.class);

    @Mock
    private ClassificationGroupRepository classificationGroupRepository;

    @Mock
    private AlgorithmAccuracyRepository algorithmAccuracyRepository;

    @Mock
    private ResultRepository resultRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSendAlgorithmProcessingRequest() {
        //given
        final Algorithm algorithm = Algorithm.CROSS_VALIDATION;
        final UUID searchProfileId = UUID.randomUUID();
        final Optional<Integer> trainingRatio = Optional.of(90);
        final ArgumentCaptor<MessageCreator> messageCreatorCaptor = ArgumentCaptor.forClass(MessageCreator.class);
        final ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        final String destination = Queues.PROCESSING_REQUESTS;
//        doNothing().when(jobsTracker).startNextJob();

        //when
        jmsService.sendAlgorithmProcessingRequest(algorithm, searchProfileId, trainingRatio);

        //then
        verify(jmsTemplate).send(destinationCaptor.capture(), messageCreatorCaptor.capture());
        final MessageCreator capturedMessageCreator = messageCreatorCaptor.getValue();
        final String capturedDestination = destinationCaptor.getValue();
//        verify(jobsTracker, times(1)).startNextJob();
        assertEquals(destination, capturedDestination);
        assertNotNull(capturedMessageCreator);
    }

    @Test
    public void shouldReceiveMessage() throws JMSException {
        //given
        final ObjectMessage objectMessage = mock(ObjectMessage.class);
        final Algorithm algorithm = Algorithm.CROSS_VALIDATION;
        final UUID searchProfileId = UUID.randomUUID();
        final UUID resultId0 = UUID.randomUUID();
        final UUID resultId1 = UUID.randomUUID();
        final UUID resultId2 = UUID.randomUUID();
        final ClassificationGroup classificationGroup0 = ClassificationGroup.builder()
                .id(UUID.randomUUID())
                .resultId(resultId0)
                .documentDate(LocalDate.now())
                .searchProfileId(searchProfileId)
                .build();
        final ClassificationGroup classificationGroup1 = ClassificationGroup.builder()
                .id(UUID.randomUUID())
                .resultId(resultId1)
                .documentDate(LocalDate.now())
                .searchProfileId(searchProfileId)
                .build();
        final ClassificationGroup classificationGroup2 = ClassificationGroup.builder()
                .id(UUID.randomUUID())
                .resultId(resultId2)
                .documentDate(LocalDate.now())
                .searchProfileId(searchProfileId)
                .build();
        final Map<UUID, Double> resultPredictionMap = ImmutableMap.of(
                resultId0, 0.1d,
                resultId1, 1.1d,
                resultId2, 2.0d
        );
        final Integer accuracy = 60;

        final ArgumentCaptor<ClassificationGroup> captor = ArgumentCaptor.forClass(ClassificationGroup.class);

        when(objectMessage.getObject()).thenReturn((Serializable) resultPredictionMap);
        when(objectMessage.getIntProperty(DtoProperties.ACCURACY)).thenReturn(accuracy);
        when(objectMessage.getStringProperty(DtoProperties.ALGORITHM)).thenReturn(algorithm.getNormalizedName());
        when(classificationGroupRepository.findByResultId(resultId0)).thenReturn(Optional.of(classificationGroup0));
        when(classificationGroupRepository.findByResultId(resultId1)).thenReturn(Optional.of(classificationGroup1));
        when(classificationGroupRepository.findByResultId(resultId2)).thenReturn(Optional.of(classificationGroup2));
        when(resultRepository.count()).thenReturn(3L);

        //when
        jmsService.receiveMessage(objectMessage);

        //then
//        verify(jobsTracker).finishJob();
        verify(algorithmAccuracyRepository, times(resultPredictionMap.size())).createOrUpdateByAlgorithm(algorithm, accuracy, searchProfileId);
        verify(classificationGroupRepository, times(resultPredictionMap.size())).save(captor.capture());
        final List<ClassificationGroup> classificationGroups = captor.getAllValues();
        assertEquals(resultPredictionMap.size(), classificationGroups.size());
        assertEquals(Classification.NEGATIVE, classificationGroups.get(0).getCrossValidatorClassification());
        assertEquals(Classification.POSITIVE, classificationGroups.get(1).getCrossValidatorClassification());
    }
}
