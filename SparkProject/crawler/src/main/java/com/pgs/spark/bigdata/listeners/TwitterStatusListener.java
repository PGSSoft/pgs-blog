package com.pgs.spark.bigdata.listeners;

import com.pgs.spark.bigdata.domain.Tag;
import com.pgs.spark.bigdata.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Twitter status listener.
 */
@Component
public class TwitterStatusListener implements StatusListener {

    private final static String twitterUrlPrefix = "https://twitter.com/statuses/";

    @Autowired
    private DocumentService documentService;

    @Override
    public void onStatus(final Status status) {
        LocalDate tweetDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(status.getCreatedAt()));
        String tweetUrl = twitterUrlPrefix + status.getId();
        Set<Tag> tags = new HashSet<>();
        if (status.getHashtagEntities() != null) {
            for (HashtagEntity tag : status.getHashtagEntities()) {
                if (!tag.getText().isEmpty()) {
                    tags.add(Tag.builder().content(tag.getText()).build());
                }
            }
        }
        documentService.addDocument(tweetUrl, status.getText(), tweetDate, null, tags);
    }

    @Override
    public void onDeletionNotice(final StatusDeletionNotice statusDeletionNotice) {
    }

    @Override
    public void onTrackLimitationNotice(int i) {
    }

    @Override
    public void onScrubGeo(long l, long l1) {
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {
    }

    @Override
    public void onException(Exception e) {
    }
}
