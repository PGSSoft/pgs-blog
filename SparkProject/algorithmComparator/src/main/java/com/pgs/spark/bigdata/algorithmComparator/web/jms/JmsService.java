package com.pgs.spark.bigdata.algorithmComparator.web.jms;


import com.pgs.spark.bigdata.algorithmComparator.domain.Algorithm;
import com.pgs.spark.bigdata.algorithmComparator.domain.Classification;
import com.pgs.spark.bigdata.algorithmComparator.domain.ClassificationGroup;
import com.pgs.spark.bigdata.algorithmComparator.domain.Result;
import com.pgs.spark.bigdata.algorithmComparator.repository.AlgorithmAccuracyRepository;
import com.pgs.spark.bigdata.algorithmComparator.repository.ClassificationGroupRepository;
import com.pgs.spark.bigdata.algorithmComparator.repository.ResultRepository;
import com.pgs.spark.bigdata.algorithmComparator.web.jms.util.DtoProperties;
import com.pgs.spark.bigdata.algorithmComparator.web.jms.util.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class JmsService {
    private static final Logger log = LoggerFactory.getLogger(JmsService.class);

    @Autowired
    private ClassificationGroupRepository classificationGroupRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private AlgorithmAccuracyRepository algorithmAccuracyRepository;

    public void sendAlgorithmProcessingRequest(Algorithm algorithm, UUID searchProfileId, Optional<Integer> trainingRatio) {
        MessageCreator messageCreator = session -> {
            Message message = session.createMessage();
            message.setStringProperty(DtoProperties.ALGORITHM, algorithm.getNormalizedName());
            message.setStringProperty(DtoProperties.SEARCH_PROFILE, searchProfileId.toString());
            message.setIntProperty(DtoProperties.TRAINING_RATIO, trainingRatio.orElse(100));
            return message;
        };
        jmsTemplate.send(Queues.PROCESSING_REQUESTS, messageCreator);
    }

    @JmsListener(destination = Queues.PROCESSING_RESULTS)
    @SuppressWarnings(value = "unchecked")
    public void receiveMessage(ObjectMessage message) throws JMSException {
        Map<UUID, Double> resultPredictionMap = (Map<UUID, Double>) message.getObject();
        Integer accuracy = message.getIntProperty(DtoProperties.ACCURACY);
        if (resultRepository.count() != 0) {
            Algorithm.fromValue(message.getStringProperty(DtoProperties.ALGORITHM)).ifPresent(algorithm -> {
                resultPredictionMap.forEach((UUID resultId, Double prediction) -> {
                    ClassificationGroup classificationGroup = getClassificationGroup(resultId);
                    Classification classification = Classification.fromLabelLinear(prediction);
                    setProperClassificationFromAlgorithm(classificationGroup, algorithm, classification, accuracy);
                    classificationGroupRepository.save(classificationGroup);
                });
            });
        }
    }

    private ClassificationGroup getClassificationGroup(final UUID resultId) {
        return classificationGroupRepository.findByResultId(resultId).orElseGet(() -> {
            Result result = resultRepository.getOneByResultId(resultId);
            return ClassificationGroup.builder()
                    .resultId(resultId)
                    .documentDate(result.getDocumentDate())
                    .searchProfileId(result.getSearchProfileId())
                    .build();
        });
    }

    private void setProperClassificationFromAlgorithm(ClassificationGroup classificationGroup, Algorithm algorithm, Classification classification, int accuracy) {
        algorithmAccuracyRepository.createOrUpdateByAlgorithm(algorithm, accuracy, classificationGroup.getSearchProfileId());
        switch (algorithm) {
            case CROSS_VALIDATION:
                classificationGroup.setCrossValidatorClassification(classification);
                break;
            case SIMPLE:
                classificationGroup.setSimpleClassification(classification);
                break;
            case TRAIN_VALIDATION:
                classificationGroup.setTrainValidatorClassification(classification);
                break;
            case MULTILAYER_PERCEPTRON:
                classificationGroup.setMultilayerPerceptronClassification(classification);
                break;
        }
    }
}
