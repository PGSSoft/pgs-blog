package com.pgs.spark.bigdata.algorithmComparator.repository;

import com.pgs.spark.bigdata.algorithmComparator.Application;
import com.pgs.spark.bigdata.algorithmComparator.domain.Document;
import org.apache.commons.lang3.RandomStringUtils;
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
public class DocumentRepositoryIT {

    @Autowired
    private DocumentRepository documentRepository;

    private Document testDocument;

    @Before
    public void setUp() {
        testDocument = new Document();
        testDocument.setContent("Content");
        testDocument.setCreationDate(LocalDate.of(1000, 1, 1));
        testDocument.setUpdateDate(LocalDate.now());
        testDocument.setUrl("http://www.url.com/" + RandomStringUtils.randomAlphanumeric(10));
        testDocument = documentRepository.save(testDocument);
    }

    @After
    public void tearDown() {
        documentRepository.delete(testDocument);
    }

    @Test
    public void shouldFindDocumentByUrl() {
        //given
        final String url = testDocument.getUrl();

        //when
        final Document document = documentRepository.findByUrl(url);

        //then
        assertEquals(testDocument, document);
    }
}
