package com.pgs.spark.bigdata.web.rest.mapper;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.web.rest.dto.DocumentDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper for the entity Document and its DTO DocumentDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DocumentMapper {

    DocumentDTO documentToDocumentDTO(Document document);

    List<DocumentDTO> documentsToDocumentDTOs(List<Document> documents);

    Document documentDTOToDocument(DocumentDTO documentDTO);

    List<Document> documentDTOsToDocuments(List<DocumentDTO> documentDTOs);
}
