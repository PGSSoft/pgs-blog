package com.pgs.spark.bigdata.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.service.DocumentService;
import com.pgs.spark.bigdata.web.rest.dto.DocumentDTO;
import com.pgs.spark.bigdata.web.rest.mapper.DocumentMapper;
import com.pgs.spark.bigdata.web.rest.util.HeaderUtil;
import com.pgs.spark.bigdata.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for managing Document.
 */
@RestController
@RequestMapping("/api")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;

    /**
     * POST  /documents : Create a new document.
     *
     * @param documentDTO the documentDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new documentDTO, or with status 400 (Bad Request) if the document has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/documents",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentDTO> createDocument(@Valid @RequestBody DocumentDTO documentDTO) throws URISyntaxException {
        log.debug("REST request to save Document : {}", documentDTO);
        if (documentDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("document", "idexists", "A new document cannot already have an ID")).body(null);
        }
        final DocumentDTO result = documentService.save(documentDTO);
        return ResponseEntity.created(new URI("/api/documents/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("document", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /documents : Updates an existing document.
     *
     * @param documentDTO the documentDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated documentDTO,
     * or with status 400 (Bad Request) if the documentDTO is not valid,
     * or with status 500 (Internal Server Error) if the documentDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/documents",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentDTO> updateDocument(@Valid @RequestBody DocumentDTO documentDTO) throws URISyntaxException {
        log.debug("REST request to update Document : {}", documentDTO);
        if (documentDTO.getId() == null) {
            return createDocument(documentDTO);
        }
        final DocumentDTO result = documentService.save(documentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("document", documentDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /documents : get all the documents.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of documents in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/documents",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<DocumentDTO>> getAllDocuments(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Documents");
        final Page<Document> page = documentService.findAll(pageable);
        final HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/documents");
        return new ResponseEntity<>(documentMapper.documentsToDocumentDTOs(page.getContent()), headers, HttpStatus.OK);
    }

    /**
     * GET  /documents/:id : get the "id" document.
     *
     * @param id the id of the documentDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the documentDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/documents/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable UUID id) {
        log.debug("REST request to get Document : {}", id);
        final DocumentDTO documentDTO = documentService.findOne(id);
        return Optional.ofNullable(documentDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /documents/:id : delete the "id" document.
     *
     * @param id the id of the documentDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/documents/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        log.debug("REST request to delete Document : {}", id);
        documentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("document", id.toString())).build();
    }

    @RequestMapping(value = "/documents/shuffleDates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> shuffleDates(@RequestParam("from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
                                             @RequestParam(value = "until", required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate until) {
        log.debug("REST request to shuffle Documents' dates");
        documentService.shuffleDocumentsDates(from, Optional.ofNullable(until).orElse(LocalDate.now()));
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("document", Long.toString(0))).build();
    }

    @RequestMapping(value = "/documents/list",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Map<UUID, DocumentDTO>> getDocumentsByIds(@RequestParam("documentsIds") List<UUID> documentsIds) {
        log.debug("REST request to get list of documents by ids");
        final List<DocumentDTO> documentDTOs = documentService.listByIds(documentsIds);
        return ResponseEntity.ok(documentDTOs.stream().collect(Collectors.toMap(DocumentDTO::getId, document -> document)));
    }
}
