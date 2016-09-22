package com.pgs.spark.bigdata.service.impl;

import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.domain.SocialMedia;
import com.pgs.spark.bigdata.repository.SearchCriteriaRepository;
import com.pgs.spark.bigdata.service.SocialMediaService;
import com.pgs.spark.bigdata.service.socialMediaCrawling.SocialMediaCrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The type Social media service.
 */
@Service
public class SocialMediaServiceImpl implements SocialMediaService {

    @Autowired
    private SearchCriteriaRepository searchCriteriaRepository;

    @Resource(name = "mediaServiceMap")
    private Map<SocialMedia, SocialMediaCrawlingService> mediaCrawlingServiceMap;

    @Override
    public List<String> listSocialMedias() {
        return SocialMedia.stringValues();
    }

    @Override
    public void performCrawling(final String website, final UUID searchProfileId) {
        String normalizedWebsite = website.toUpperCase();
        if (SocialMedia.stringValues().contains(normalizedWebsite)) {
            List<SearchCriteria> searchCriterias = searchCriteriaRepository.findAllBySearchProfileId(searchProfileId);
            Set<String> keywords = searchCriterias.stream().map(SearchCriteria::getKeyWord).collect(Collectors.toSet());
            mediaCrawlingServiceMap.get(SocialMedia.valueOf(normalizedWebsite)).crawl(keywords);
        }
    }
}
