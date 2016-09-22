package com.pgs.spark.bigdata.service;

import com.google.common.collect.ImmutableMap;
import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.domain.SocialMedia;
import com.pgs.spark.bigdata.repository.SearchCriteriaRepository;
import com.pgs.spark.bigdata.service.impl.SocialMediaServiceImpl;
import com.pgs.spark.bigdata.service.socialMediaCrawling.SocialMediaCrawlingService;
import com.pgs.spark.bigdata.service.socialMediaCrawling.TwitterCrawlingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.ReflectionUtils.setField;

@RunWith(MockitoJUnitRunner.class)
public class SocialMediaServiceImplTest {

    @InjectMocks
    private SocialMediaServiceImpl socialMediaService = new SocialMediaServiceImpl();

    @Mock
    private TwitterCrawlingService twitterCrawlingService;

    private Map<SocialMedia, SocialMediaCrawlingService> crawlingServiceMap;

    @Mock
    private SearchCriteriaRepository searchCriteriaRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        crawlingServiceMap = ImmutableMap.of(
                SocialMedia.TWITTER, twitterCrawlingService
        );

        Field field = ReflectionUtils.findField(SocialMediaServiceImpl.class, "mediaCrawlingServiceMap");
        ReflectionUtils.makeAccessible(field);
        setField(field, socialMediaService, crawlingServiceMap);
    }

    @Test
    public void shouldListSocialMedias() {
        //when
        List<String> socialMedias = socialMediaService.listSocialMedias();

        //then
        assertEquals(socialMedias, SocialMedia.stringValues());
    }

    @Test
    public void shouldStartCrawling() {
        //given
        UUID id = UUID.randomUUID();
        Set<String> keywords = new HashSet<>(Arrays.asList("Duda", "Tusk", "Komorowski"));
        List<SearchCriteria> searchCriterias = keywords.stream().map(keyword -> SearchCriteria.builder().keyWord(keyword).build()).collect(Collectors.toList());

        when(searchCriteriaRepository.findAllBySearchProfileId(id)).thenReturn(searchCriterias);
        doNothing().when(twitterCrawlingService).crawl(keywords);

        //when
        socialMediaService.performCrawling(SocialMedia.TWITTER.name(), id);

        //then
        verify(twitterCrawlingService).crawl(keywords);
    }
}
