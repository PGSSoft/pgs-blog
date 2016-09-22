package com.pgs.spark.bigdata.listeners;

import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.service.DocumentService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import twitter4j.HashtagEntity;
import twitter4j.Status;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TwitterStatusListenerTest {

    private final static String twitterUrlPrefix = "https://twitter.com/statuses/";

    @InjectMocks
    private TwitterStatusListener twitterStatusListener = new TwitterStatusListener();

    @Mock
    private DocumentService documentService;

    @Test
    public void shouldSaveDocumentOnStatusReceived() {
        //given
        final Status status = buildStatus();
        final String url = twitterUrlPrefix + status.getId();
        final LocalDate tweetDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(status.getCreatedAt()));

        //when
        twitterStatusListener.onStatus(status);

        //then
        verify(status, times(2)).getId();
        verify(status, times(2)).getCreatedAt();
        verify(status, times(1)).getText();
        verify(documentService).addDocument(url, status.getText(), tweetDate, null, Collections.emptySet());
    }

    @Test
    public void shouldSaveDocumentWithTagsOnStatusReceived() {
        //given
        final Status status = buildStatus();
        final String url = twitterUrlPrefix + status.getId();
        final LocalDate tweetDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(status.getCreatedAt()));
        final HashtagEntity[] tagEntities = buildHashTagEntities(3);
        when(status.getHashtagEntities()).thenReturn(tagEntities);
        final Set<Tag> tags = new HashSet<>();
        for (HashtagEntity tag : tagEntities) {
            if (!tag.getText().isEmpty()) {
                tags.add(Tag.builder().content(tag.getText()).build());
            }
        }

        //when
        twitterStatusListener.onStatus(status);

        //then
        verify(status, times(2)).getId();
        verify(status, times(2)).getCreatedAt();
        verify(status, times(1)).getText();
        verify(documentService).addDocument(url, status.getText(), tweetDate, null, tags);
    }

    private Status buildStatus() {
        final Status status = Mockito.mock(Status.class);
        final Random random = new Random();
        when(status.getCreatedAt()).thenReturn(new Date());
        when(status.getId()).thenReturn(random.nextLong());
        return status;
    }

    private HashtagEntity[] buildHashTagEntities(final int numberOfEntities) {
        final HashtagEntity[] entities = new HashtagEntity[numberOfEntities];
        final Random random = new Random();
        for (int i = 0; i < numberOfEntities; i++) {
            final HashtagEntity entity = new TestHashTagEntity(
                    RandomStringUtils.randomAlphabetic(10),
                    random.nextInt(),
                    random.nextInt()
            );
            entities[i] = entity;
        }
        return entities;
    }

    private class TestHashTagEntity implements HashtagEntity {
        private final String text;
        private final int start;
        private final int end;

        TestHashTagEntity(final String text, final int start, final int end) {
            this.text = text;
            this.start = start;
            this.end = end;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public int getStart() {
            return start;
        }

        @Override
        public int getEnd() {
            return end;
        }
    }
}
