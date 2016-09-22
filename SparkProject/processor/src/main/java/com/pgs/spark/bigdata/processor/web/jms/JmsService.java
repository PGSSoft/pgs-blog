package com.pgs.spark.bigdata.processor.web.jms;

import com.pgs.spark.bigdata.processor.domain.Algorithm;
import com.pgs.spark.bigdata.processor.jobs.SparkJob;
import com.pgs.spark.bigdata.processor.web.jms.util.DtoProperties;
import com.pgs.spark.bigdata.processor.web.jms.util.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * The type Jms service.
 */
@Component
public class JmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsService.class);

    @Resource(name = "algorithmsJobsMap")
    private Map<Algorithm, SparkJob> jobs;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * Receive request.
     *
     * @param message the message
     * @throws JMSException the jms exception
     */
    @JmsListener(destination = Queues.PROCESSING_REQUESTS)
    public void receiveRequest(Message message) throws JMSException {
        LOGGER.debug("Received <" + message.getStringProperty(DtoProperties.ALGORITHM) + "> : <" + message.getStringProperty(DtoProperties.SEARCH_PROFILE) + ">");
        UUID searchProfileId = UUID.fromString(message.getStringProperty(DtoProperties.SEARCH_PROFILE));
        Integer trainingRatio = message.getIntProperty(DtoProperties.TRAINING_RATIO);
        Algorithm.fromValue(message.getStringProperty(DtoProperties.ALGORITHM))
                .ifPresent(algorithm -> jobs.get(algorithm).withTrainingRatio(trainingRatio).getPredictions(searchProfileId)
                        .thenAccept(optionalEstimationDTO ->
                            optionalEstimationDTO.ifPresent(estimationDTO -> {
                            LOGGER.debug("Training ratio: {}, estimation accuracy: {}", estimationDTO.getTrainingRatio(), estimationDTO.getEstimationAccuracy());
                            sendResponse(algorithm, estimationDTO.getEstimationAccuracy(), estimationDTO.getResultPredictionMap());
        })));
    }

    /**
     * Send response.
     *
     * @param algorithm   the algorithm
     * @param accuracy    the accuracy
     * @param predictions the predictions
     */
    public void sendResponse(final Algorithm algorithm, final Integer accuracy, final Map<UUID, Double> predictions) {
        MessageCreator messageCreator = session -> {
            ObjectMessage message = session.createObjectMessage();
            message.setStringProperty(DtoProperties.ALGORITHM, algorithm.getNormalizedName());
            message.setIntProperty(DtoProperties.ACCURACY, accuracy);
            message.setObject((Serializable) predictions);
            return message;
        };
        jmsTemplate.send(Queues.PROCESSING_RESULTS, messageCreator);
    }
}
