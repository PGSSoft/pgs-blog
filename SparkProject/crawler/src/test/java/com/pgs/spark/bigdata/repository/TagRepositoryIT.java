package com.pgs.spark.bigdata.repository;

import com.pgs.spark.bigdata.Application;
import com.pgs.spark.bigdata.domain.Document;
import com.pgs.spark.bigdata.domain.Tag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class TagRepositoryIT {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private Document document;

    private Tag tag;

    @Before
    public void setUp() {
        document = new Document();
        document.setClassification("POSITIVE");
        document.setUrl("http://www.url.com");
        document.setCreationDate(LocalDate.of(2000, 1, 1));
        document.setUpdateDate(LocalDate.now());
        document.setTitle("title");
        document.setContent("content");
        document.setTags("tagContent");
        document = documentRepository.save(document);

        tag = new Tag();
        tag.setDocumentId(document.getId());
        tag.setContent("tagContent");
        tag = tagRepository.save(tag);
    }

    @After
    public void tearDown() {
        tagRepository.delete(tag);
        documentRepository.delete(document);
    }

    @Test
    public void shouldFindTagById() {
        //when
        final Tag testedTag = tagRepository.findOne(tag.getId());

        //then
        assertEquals(tag, testedTag);
    }
}
