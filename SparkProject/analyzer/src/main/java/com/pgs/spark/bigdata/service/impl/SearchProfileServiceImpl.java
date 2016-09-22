package com.pgs.spark.bigdata.service.impl;

import com.pgs.spark.bigdata.domain.SearchProfile;
import com.pgs.spark.bigdata.domain.User;
import com.pgs.spark.bigdata.repository.SearchProfileRepository;
import com.pgs.spark.bigdata.repository.UserRepository;
import com.pgs.spark.bigdata.security.SecurityUtils;
import com.pgs.spark.bigdata.service.SearchProfileService;
import com.pgs.spark.bigdata.web.rest.dto.SearchProfileDTO;
import com.pgs.spark.bigdata.web.rest.mapper.SearchProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing SearchProfile.
 */
@Service
@Transactional
public class SearchProfileServiceImpl implements SearchProfileService {

    private final Logger log = LoggerFactory.getLogger(SearchProfileServiceImpl.class);

    @Inject
    private SearchProfileRepository searchProfileRepository;

    @Inject
    private SearchProfileMapper searchProfileMapper;

    @Inject
    private UserRepository userRepository;

    /**
     * Save a searchProfile.
     *
     * @param searchProfileDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public SearchProfileDTO save(SearchProfileDTO searchProfileDTO) {
        log.debug("Request to save SearchProfile : {}", searchProfileDTO);
        SearchProfile searchProfile = searchProfileMapper.searchProfileDTOToSearchProfile(searchProfileDTO);
        Optional<User> userOptional = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        if(userOptional.isPresent()){
            searchProfile.setUserId(userOptional.get().getId());
        }
        searchProfile = searchProfileRepository.save(searchProfile);
        final SearchProfileDTO result = searchProfileMapper.searchProfileToSearchProfileDTO(searchProfile);
        return result;
    }

    /**
     * Get all the searchProfiles.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<SearchProfileDTO> findAll() {
        log.debug("Request to get all SearchProfiles");
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        final List<SearchProfileDTO> result = searchProfileRepository.findAll(user.getId()).stream()
            .map(searchProfileMapper::searchProfileToSearchProfileDTO)
            .collect(Collectors.toCollection(LinkedList::new));
        return result;
    }

    /**
     * Get one searchProfile by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public SearchProfileDTO findOne(UUID id) {
        log.debug("Request to get SearchProfile : {}", id);
        final SearchProfile searchProfile = searchProfileRepository.findOne(id);
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        if(searchProfile == null || !searchProfile.getUserId().equals(user.getId())){
            return null;
        }
        return searchProfileMapper.searchProfileToSearchProfileDTO(searchProfile);
    }

    /**
     * Delete the  searchProfile by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete SearchProfile : {}", id);
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        final SearchProfile searchProfile = searchProfileRepository.findOne(id);
        if(searchProfile.getUserId().equals(user.getId())) {
            searchProfileRepository.delete(id);
        }
    }
}
