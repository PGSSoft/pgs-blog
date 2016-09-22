package com.pgs.spark.bigdata.service;

import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.repository.DocumentRepository;
import com.pgs.spark.bigdata.repository.TagRepository;
import com.pgs.spark.bigdata.service.impl.DocumentServiceImpl;
import com.pgs.spark.bigdata.dto.ArticleDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {

    @Mock
    private DocumentRepository repository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddDocumentStringArticleTO() throws Exception {
        // given
        final String url = "www.bankier.pl";
        final UUID documentId = UUID.randomUUID();
        final String content = RandomStringUtils.random(100);
        final Set<Tag> tags = Stream.generate(() -> RandomStringUtils.random(5)).limit(5).collect(Collectors.toSet())
                .stream().map(tagContent -> Tag.builder().content(tagContent).documentId(documentId).build()).collect(Collectors.toSet());
        final String title = RandomStringUtils.random(10);
        final LocalDate date = LocalDate.now();

        final ArticleDTO articleContent = new ArticleDTO();
        articleContent.setContent(content);
        articleContent.setTags(tags);
        articleContent.setTitle(title);
        articleContent.setDate(date);

        final Document document = new Document();
        document.setContent(content);
        document.setUrl(url);
        document.setCreationDate(date);
        document.setTitle(articleContent.getTitle());
        document.setCreationDate(articleContent.getDate());

        final String tagsString = articleContent.getTags().size() > 0 ? articleContent
                .getTags()
                .stream()
                .map(Tag::getContent)
                .distinct()
                .collect(Collectors.joining(","))
                : "";

        document.setTags(tagsString);

        final Document savedDocument = new Document();
        savedDocument.setId(UUID.randomUUID());
        savedDocument.setContent(content);
        savedDocument.setUrl(url);
        savedDocument.setCreationDate(date);
        savedDocument.setUpdateDate(date);

        when(repository.save(any(Document.class))).thenReturn(savedDocument);

        // when
        documentService.addDocument(url, articleContent);

        // then
        verify(repository).save(document);
        verify(tagRepository).save(tags);

    }

    @Test
    public void testAddDocumentStringStringDateDate() throws Exception {
        // given
        final String url = "www.bankier.pl";
        final String content = RandomStringUtils.random(10);
        final LocalDate updateDate = LocalDate.now();
        final LocalDate creationDate = LocalDate.parse("2014-10-10");

        // when
        documentService.addDocument(url, content, creationDate, updateDate);

        // then
        final Document document = new Document();
        document.setUrl(url);
        document.setContent(content);
        document.setUrl(url);
        document.setCreationDate(creationDate);
        document.setUpdateDate(updateDate);

        verify(repository).save(document);
    }

}
