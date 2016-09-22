package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.web.rest.dto.DocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service Interface for managing Document.
 */
public interface DocumentService {

    /**
     * Save a document.
     *
     * @param documentDTO the entity to save
     * @return the persisted entity
     */
    DocumentDTO save(DocumentDTO documentDTO);

    /**
     * Get all the documents.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Document> findAll(Pageable pageable);

    /**
     * Get the "id" document.
     *
     * @param id the id of the entity
     * @return the entity
     */
    DocumentDTO findOne(UUID id);

    /**
     * Delete the "id" document.
     *
     * @param id the id of the entity
     */
    void delete(UUID id);

    /**
     * Randomizes updateDate of all Documents in DB. Made for sake of testing before a real data is being collected.
     *
     * @param from  from range
     * @param until until range
     */
    void shuffleDocumentsDates(final LocalDate from, final LocalDate until);

    /**
     * Get documents by ids
     *
     * @param documentsIds list of documents ids
     */
    List<DocumentDTO> listByIds(List<UUID> documentsIds);
}
