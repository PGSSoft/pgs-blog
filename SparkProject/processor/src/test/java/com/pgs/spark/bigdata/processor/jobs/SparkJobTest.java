package com.pgs.spark.bigdata.processor.jobs;

import com.pgs.spark.bigdata.processor.ProcessorApplication;
import com.pgs.spark.bigdata.processor.domain.Classification;
import com.pgs.spark.bigdata.processor.domain.MultilayerPerceptronClassifierProperties;
import com.pgs.spark.bigdata.processor.domain.Result;
import com.pgs.spark.bigdata.processor.dto.AlgorithmEstimationDTO;
import com.pgs.spark.bigdata.processor.dto.ResultPKDTO;
import com.pgs.spark.bigdata.processor.jobs.estimationJobs.MultilayerPerceptronClassifierJob;
import com.pgs.spark.bigdata.processor.mappers.ResultMappers;
import com.pgs.spark.bigdata.processor.repository.ResultRepository;
import org.apache.spark.sql.SQLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProcessorApplication.class)
@WebAppConfiguration
@PropertySource("classpath:application.properties")
public class SparkJobTest {

    @InjectMocks
    private SparkJob sparkJob = new MultilayerPerceptronClassifierJob();

    @Autowired
    private SQLContext sqlContext;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private ResultMappers resultMappers;

    protected int[] numFeaturesTable = new int[]{5,10,15};

    protected double[] regParamTable = new double[]{0.1,0.01};

    protected double[] elasticNetParamTable = new double[]{0.0,0.5,1.0};

    protected int[] maxIterTable = new int[]{50, 100};

    final String[] commonWords = {"the","of","and","a","to","in","is","you","that","it","he","was","for","on","are","as","with","his","they","I","at","be","this","have","from","or","one","had","by","word","but","not","what","all","were","we","when","your","can","said","there","use","an","each","which","she","do","how","their","if","will","up","other","about","out","many","then","them","these","so","some","her","would","make","like","him","into","time","has","look","two","more","write","go","see","number","no","way","could","people","my","than","first","water","been","call","who","oil","its","now","find","long","down","day","did","get","come","made","may","part"};

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        when(resultMappers.toContent(any())).thenCallRealMethod();
        when(resultMappers.toLabeledContent(any())).thenCallRealMethod();

        ReflectionTestUtils.setField(sparkJob, "properties", new MultilayerPerceptronClassifierProperties(
                3, 100, 1234, 256, 100, new int[]{8,10,15}
        ));
        ReflectionTestUtils.setField(sparkJob, "sqlContext", sqlContext);
        ReflectionTestUtils.setField(sparkJob, "numFeaturesTable", numFeaturesTable);
        ReflectionTestUtils.setField(sparkJob, "regParamTable", regParamTable);
        ReflectionTestUtils.setField(sparkJob, "elasticNetParamTable", elasticNetParamTable);
        ReflectionTestUtils.setField(sparkJob, "maxIterTable", maxIterTable);
    }

    @Test
    public void shouldReturnPredictions() throws InterruptedException, ExecutionException, TimeoutException {
        //given
        final int trainingRatio = 90;
        final UUID searchProfileId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        when(resultRepository.findBySearchProfileToTraining(searchProfileId)).thenReturn(buildResults(50, searchProfileId, documentId, true));
        when(resultRepository.findBySearchProfileToEstimating(eq(searchProfileId), any()))
                .thenReturn(buildResults(40, searchProfileId, documentId, false))
                .thenReturn(buildResults(40, searchProfileId, documentId, false))
                .thenReturn(buildResults(40, searchProfileId, documentId, false))
                .thenReturn(Collections.emptyList());

        //when
        final Optional<AlgorithmEstimationDTO> estimationDTO = sparkJob.withTrainingRatio(trainingRatio).getPredictions(searchProfileId).get(10, TimeUnit.MINUTES);

        //then
        assertTrue(estimationDTO.isPresent());
        assertTrue(trainingRatio == estimationDTO.get().getTrainingRatio());
        assertNotNull(estimationDTO.get().getResultPredictionMap());
        assertTrue(estimationDTO.get().getResultPredictionMap().size() == 120);
    }

    @Test
    public void shouldRunSparkJob() throws InterruptedException {
        //given
        final UUID searchProfileId = UUID.randomUUID();
        final UUID documentId = UUID.randomUUID();
        final List<Result> results0 = buildResults(40, searchProfileId, documentId, false);
        final List<Result> results1 = buildResults(40, searchProfileId, documentId, false);
        final List<Result> results2 = buildResults(40, searchProfileId, documentId, false);

        when(resultRepository.findBySearchProfileToTraining(searchProfileId)).thenReturn(buildResults(50, searchProfileId, documentId, true));
        when(resultRepository.findBySearchProfileToEstimating(eq(searchProfileId), any()))
                .thenReturn(results0)
                .thenReturn(results1)
                .thenReturn(results2)
                .thenReturn(Collections.emptyList());

        //when
        sparkJob.run(searchProfileId);

        Thread.sleep(10000);

        //then
        verify(resultRepository, times(3 * Classification.values().length)).updateClassification(any(List.class), any());
    }

    private List<Result> buildResults(final int count, final UUID searchProfileId, final UUID documentId, final boolean classified){
        final int numberOfWordsInSentence = 50;
        final List<Result> results = new ArrayList<>();
        final Random random = new Random();
        for(int i = 0 ; i < count ; i++){
            Result.ResultBuilder builder = Result.builder();
            builder.id(UUID.randomUUID())
                    .searchProfileId(searchProfileId)
                    .documentId(documentId);

            if(classified) {
                builder.classification(Classification.values()[random.nextInt(Classification.values().length)]);
            }

            final StringBuilder stringBuilder = new StringBuilder();
            for(int j = 0 ; j < numberOfWordsInSentence ; j++){
                stringBuilder.append(commonWords[random.nextInt(commonWords.length)]).append(" ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

            builder.documentContent(stringBuilder.toString());

            results.add(builder.build());
        }
        return results;
    }
}
