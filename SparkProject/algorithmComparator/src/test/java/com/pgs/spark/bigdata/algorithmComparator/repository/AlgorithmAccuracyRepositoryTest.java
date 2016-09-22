package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.datastax.driver.core.Session;
import com.pgs.spark.bigdata.algorithmComparator.domain.Algorithm;
import com.pgs.spark.bigdata.algorithmComparator.domain.AlgorithmAccuracy;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlgorithmAccuracyRepositoryTest {

    @Mock
    private AlgorithmAccuracyRepository algorithmAccuracyRepository;

    @Test
    public void shouldCreateAlgorithm() {
        //given
        final Algorithm algorithm = Algorithm.CROSS_VALIDATION;
        final int accuracy = 80;
        final AlgorithmAccuracy algorithmAccuracy = new AlgorithmAccuracy();
        final UUID searchProfileId = UUID.randomUUID();
        algorithmAccuracy.setAccuracy(80);
        algorithmAccuracy.setName(algorithm.name());
        when(algorithmAccuracyRepository.getByAlgorithm(eq(algorithm), any())).thenReturn(Optional.empty());
        doCallRealMethod().when(algorithmAccuracyRepository).createOrUpdateByAlgorithm(eq(algorithm), eq(accuracy), any());

        //when
        algorithmAccuracyRepository.createOrUpdateByAlgorithm(algorithm, accuracy, searchProfileId);

        //then
        final ArgumentCaptor<AlgorithmAccuracy> captor = ArgumentCaptor.forClass(AlgorithmAccuracy.class);
        verify(algorithmAccuracyRepository).save(captor.capture());
        assertNull(captor.getValue().getId());
        assertEquals(algorithmAccuracy.getAccuracy(), captor.getValue().getAccuracy());
        assertEquals(algorithmAccuracy.getName(), captor.getValue().getName());
    }

    @Test
    public void shouldUpdateAlgorithm() {
        //given
        final Algorithm algorithm = Algorithm.CROSS_VALIDATION;
        final int accuracy = 80;
        final AlgorithmAccuracy algorithmAccuracy = new AlgorithmAccuracy();
        final UUID searchProfileId = UUID.randomUUID();
        algorithmAccuracy.setAccuracy(80);
        algorithmAccuracy.setName(algorithm.name());
        when(algorithmAccuracyRepository.getByAlgorithm(algorithm, searchProfileId)).thenReturn(Optional.of(AlgorithmAccuracy.builder()
                .id(UUID.randomUUID())
                .accuracy(accuracy - 30)
                .name(RandomStringUtils.randomAlphabetic(10))
                .build()
        ));

        doNothing().when(algorithmAccuracyRepository).update(any(AlgorithmAccuracy.class));
        doCallRealMethod().when(algorithmAccuracyRepository).createOrUpdateByAlgorithm(algorithm, accuracy, searchProfileId);

        //when
        algorithmAccuracyRepository.createOrUpdateByAlgorithm(algorithm, accuracy, searchProfileId);

        //then
        final ArgumentCaptor<AlgorithmAccuracy> captor = ArgumentCaptor.forClass(AlgorithmAccuracy.class);
        verify(algorithmAccuracyRepository).update(captor.capture());
        assertNotNull(captor.getValue().getId());
        assertNotNull(captor.getValue().getAccuracy());
        assertNotNull(captor.getValue().getName());
    }
}
