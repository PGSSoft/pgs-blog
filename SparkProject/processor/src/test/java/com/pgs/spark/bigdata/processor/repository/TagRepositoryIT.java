package com.pgs.spark.bigdata.processor.repository;

import com.pgs.spark.bigdata.processor.ProcessorApplication;
import com.pgs.spark.bigdata.processor.domain.Document;
import com.pgs.spark.bigdata.processor.domain.Tag;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ProcessorApplication.class)
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
        document.setUrl("http://www.url.com");
        document.setCreationDate(LocalDate.of(2000, 1, 1));
        document.setUpdateDate(LocalDate.now());
        document.setContent("content");
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

    @Test
    public void shouldFindSuperTagByChild() {
        //given
        Tag superTag = Tag.builder()
                .isSuperTag(true)
                .content(RandomStringUtils.randomAlphabetic(10))
                .documentId(document.getId())
                .build();

        superTag = tagRepository.save(superTag);

        Tag childTag = Tag.builder()
                .parentId(superTag.getId())
                .content(RandomStringUtils.randomAlphabetic(10))
                .documentId(document.getId())
                .build();

        childTag = tagRepository.save(childTag);

        //when
        final Optional<Tag> testedTag = tagRepository.findSuperTagByChild(childTag);

        //then
        assertTrue(testedTag.isPresent());
        assertEquals(superTag, testedTag.get());

        tagRepository.delete(superTag);
        tagRepository.delete(childTag);
    }

    @Test
    public void shouldFindSuperTags(){
        //given
        List<Tag> superTags = buildTags(document.getId(), true).limit(10).collect(Collectors.toList());
        superTags.forEach(tagRepository::save);

        //when
        final List<Tag> testedSuperTags = tagRepository.getSuperTags();

        //then
        assertTrue(testedSuperTags.containsAll(superTags));

        superTags.forEach(tagRepository::delete);
    }

    @Test
    public void shouldFindChildsTagsOfGivenTag(){
        //given
        final Tag superTag = tagRepository.save(Tag.builder()
                .isSuperTag(true)
                .content(RandomStringUtils.randomAlphabetic(10))
                .documentId(document.getId())
                .build());

        List<Tag> tags = buildTags(document.getId(), false).limit(10).collect(Collectors.toList());
        tags.forEach(t -> t.setParentId(superTag.getId()));
        tags.forEach(tagRepository::save);

        //when
        final List<Tag> childTags = tagRepository.getChildTags(superTag);

        //then
        assertNotNull(childTags);
        assertTrue(childTags.size() > 0);
        assertTrue(childTags.containsAll(tags));

        tags.forEach(tagRepository::delete);

    }

    @Test
    public void shouldGetTagsWithoutSuperTag(){
        //given
        List<Tag> tags = buildTags(document.getId(), false).limit(10).collect(Collectors.toList());
        tags.forEach(t -> t.setIsAssignedToSuperTag(false));
        tags.forEach(tagRepository::save);

        //when
        final List<Tag> withoutSuperTag = tagRepository.getWithoutSuperTag();

        //then
        assertNotNull(withoutSuperTag);
        assertTrue(withoutSuperTag.size() > 0);
        assertTrue(withoutSuperTag.containsAll(tags));
        tags.forEach(tagRepository::delete);
    }

    private Stream<Tag> buildTags(final UUID documentId, final boolean isSuperTag){
        return Stream.generate(() ->
            Tag.builder()
                    .documentId(documentId)
                    .content(RandomStringUtils.randomAlphabetic(10))
                    .isSuperTag(isSuperTag)
                    .build()
        );
    }
}
