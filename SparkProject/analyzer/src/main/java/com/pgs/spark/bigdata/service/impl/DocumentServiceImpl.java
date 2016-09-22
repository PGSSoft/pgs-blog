package com.pgs.spark.bigdata.service.impl;

import com.google.common.collect.Lists;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.repository.DocumentRepository;
import com.pgs.spark.bigdata.repository.util.PageRequest;
import com.pgs.spark.bigdata.service.DocumentService;
import com.pgs.spark.bigdata.service.util.RandomUtil;
import com.pgs.spark.bigdata.web.rest.dto.DocumentDTO;
import com.pgs.spark.bigdata.web.rest.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service Implementation for managing Document.
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private DocumentMapper documentMapper;

    /**
     * Save a document.
     *
     * @param documentDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public DocumentDTO save(DocumentDTO documentDTO) {
        log.debug("Request to save Document : {}", documentDTO);
        Document document = documentMapper.documentDTOToDocument(documentDTO);
        document = documentRepository.save(document);
        final DocumentDTO result = documentMapper.documentToDocumentDTO(document);
        return result;
    }

    /**
     * Get all the documents.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Document> findAll(Pageable pageable) {
        log.debug("Request to get all Documents");
        return documentRepository.findAllPageable(pageable);
    }

    /**
     * Get one document by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public DocumentDTO findOne(UUID id) {
        log.debug("Request to get Document : {}", id);
        final Document document = documentRepository.findOne(id);
        final DocumentDTO documentDTO = documentMapper.documentToDocumentDTO(document);
        return documentDTO;
    }

    /**
     * Delete the  document by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Document : {}", id);
        documentRepository.delete(id);
    }

    @Override
    public void shuffleDocumentsDates(final LocalDate from, final LocalDate until) {
        log.debug("Request to shuffle all Documents dates");
        UUID lastDocumentId = null;
        List<Document> documents = documentRepository.findAll(new PageRequest(lastDocumentId, 100));
        while (documents.size() > 0){
            documents.forEach(document -> document.setUpdateDate(RandomUtil.generateLocalDateInRange(from, until)));
            documents.forEach(documentRepository::save);
            lastDocumentId = documents.get(documents.size() - 1).getId();
            documents = documentRepository.findAll(new PageRequest(lastDocumentId, 100));
        }
    }

    @Override
    public List<DocumentDTO> listByIds(List<UUID> documentsIds) {
        return documentMapper.documentsToDocumentDTOs(documentRepository.findByIds(documentsIds));
    }
}
