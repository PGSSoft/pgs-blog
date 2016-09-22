package com.pgs.spark.bigdata.service.impl;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.repository.DocumentRepository;
import com.pgs.spark.bigdata.repository.TagRepository;
import com.pgs.spark.bigdata.service.DocumentService;
import com.pgs.spark.bigdata.dto.ArticleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Document service.
 */
@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentRepository repository;

    @Autowired
    private TagRepository tagRepository;

    @Override
    public void addDocument(final String url, final ArticleDTO articleTO) {
        final Document doc = buildDocument(url, articleTO.getTitle(), articleTO.getContent(), articleTO.getDate(), null, "", articleTO.getTags());
        final Document savedDocument = repository.save(doc);
        final Set<Tag> tags = articleTO.getTags();
        tags.forEach(tag -> tag.setDocumentId(savedDocument.getId()));
        tagRepository.save(tags);

    }

    @Override
    public Document addDocument(final String url, final String content, final LocalDate creationDate, final LocalDate updateDate) {
        final Document doc = buildDocument(url, "", content, creationDate, updateDate, "", Collections.emptySet());
        return repository.save(doc);
    }

    @Override
    public Document addDocument(String url, String content, LocalDate creationDate, LocalDate updateDate, String classification) {
        final Document doc = buildDocument(url, "", content, creationDate, updateDate, classification, Collections.emptySet());
        return repository.save(doc);
    }

    @Override
    public Document addDocument(String url, String content, LocalDate creationDate, LocalDate updateDate, Set<Tag> tags) {
        final Document doc = buildDocument(url, "", content, creationDate, updateDate, "", tags);

        final Document savedDocument = repository.save(doc);
        tags.forEach(tag -> tag.setDocumentId(savedDocument.getId()));
        tagRepository.save(tags);
        return savedDocument;
    }

    private Document buildDocument(final String url, final String title, final String content, final LocalDate creationDate, final LocalDate updateDate, final String classification, final Set<Tag> tags){
        final Document doc = Optional.ofNullable(repository.findByUrl(url)).orElse(new Document());
        doc.setContent(content);
        doc.setUrl(url);
        doc.setCreationDate(creationDate);
        doc.setUpdateDate(updateDate);
        doc.setClassification(classification);
        doc.setTitle(title);

        final String tagsString = tags.size() > 0 ? tags
                .stream()
                .map(Tag::getContent)
                .distinct()
                .collect(Collectors.joining(","))
                : "";

        doc.setTags(tagsString);

        return doc;
    }
}
