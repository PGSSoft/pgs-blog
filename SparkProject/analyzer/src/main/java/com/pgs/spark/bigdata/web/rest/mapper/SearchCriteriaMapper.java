package com.pgs.spark.bigdata.web.rest.mapper;

import com.pgs.spark.bigdata.domain.SearchCriteria;
import com.pgs.spark.bigdata.web.rest.dto.SearchCriteriaDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for the entity SearchCriteria and its DTO SearchCriteriaDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SearchCriteriaMapper {

    SearchCriteriaDTO searchCriteriaToSearchCriteriaDTO(SearchCriteria searchCriteria);

    List<SearchCriteriaDTO> searchCriteriaToSearchCriteriaDTOs(List<SearchCriteria> searchCriteria);

    SearchCriteria searchCriteriaDTOToSearchCriteria(SearchCriteriaDTO searchCriteriaDTO);

    List<SearchCriteria> searchCriteriaDTOsToSearchCriteria(List<SearchCriteriaDTO> searchCriteriaDTOs);

}
