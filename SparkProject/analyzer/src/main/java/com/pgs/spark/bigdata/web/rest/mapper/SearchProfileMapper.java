package com.pgs.spark.bigdata.web.rest.mapper;

import com.pgs.spark.bigdata.domain.SearchProfile;
import com.pgs.spark.bigdata.web.rest.dto.SearchProfileDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for the entity SearchProfile and its DTO SearchProfileDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SearchProfileMapper {

    SearchProfileDTO searchProfileToSearchProfileDTO(SearchProfile searchProfile);

    List<SearchProfileDTO> searchProfilesToSearchProfileDTOs(List<SearchProfile> searchProfiles);

    SearchProfile searchProfileDTOToSearchProfile(SearchProfileDTO searchProfileDTO);

    List<SearchProfile> searchProfileDTOsToSearchProfiles(List<SearchProfileDTO> searchProfileDTOs);
}
