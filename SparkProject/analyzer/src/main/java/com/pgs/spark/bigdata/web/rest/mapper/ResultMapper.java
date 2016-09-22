package com.pgs.spark.bigdata.web.rest.mapper;

import com.pgs.spark.bigdata.domain.Result;
import com.pgs.spark.bigdata.web.rest.dto.ResultDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper for the entity Result and its DTO ResultDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResultMapper {

    ResultDTO resultToResultDTO(Result result);

    List<ResultDTO> resultsToResultDTOs(List<Result> results);

    Result resultDTOToResult(ResultDTO resultDTO);

    void resultDTOToResult(ResultDTO resultDTO, @MappingTarget Result result);

    List<Result> resultDTOsToResults(List<ResultDTO> resultDTOs);

}
