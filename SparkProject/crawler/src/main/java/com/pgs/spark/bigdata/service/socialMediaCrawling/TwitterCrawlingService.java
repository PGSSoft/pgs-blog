package com.pgs.spark.bigdata.service.socialMediaCrawling;

import com.pgs.spark.bigdata.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * The type Twitter crawling service.
 */
@Service
public class TwitterCrawlingService extends SocialMediaCrawlingService {

    private final static String querySeparator = " OR ";

    private final static String twitterUrlPrefix = "https://twitter.com/statuses/";

    private volatile boolean streamAlreadyCrawled = false;

    @Autowired
    private TwitterStream twitterStream;

    @Autowired
    private Twitter twitter;

    @Autowired
    private StatusListener twitterListener;

    @Override
    public void crawl(final Set<String> keywords) {
        crawlTwitterApi(keywords);
        if (!streamAlreadyCrawled) {
            crawlTwitterStream(keywords);
            streamAlreadyCrawled = true;
        }
    }

    private void crawlTwitterStream(final Set<String> keywords) {
        FilterQuery tweetFilterQuery = new FilterQuery(keywords.toArray(new String[keywords.size()]));
        tweetFilterQuery.language("pl");
        twitterStream.addListener(twitterListener);
        twitterStream.filter(tweetFilterQuery);
    }

    private void crawlTwitterApi(final Set<String> keywords) {
        try {
            QueryResult queryResult = twitter.search(getQueryFromKeywords(keywords));
            parseQueryResult(queryResult);
            while (queryResult.hasNext()) {
                queryResult = twitter.search(queryResult.nextQuery());
                parseQueryResult(queryResult);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000 * 60 * 15);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    private Query getQueryFromKeywords(final Set<String> keywords) {
        StringJoiner stringJoiner = new StringJoiner(querySeparator);
        keywords.forEach(stringJoiner::add);
        Query query = new Query(stringJoiner.toString());
        query.setLang("pl");
        query.setCount(100);
        return query;
    }

    private void parseQueryResult(QueryResult queryResult) {
        queryResult.getTweets().forEach(tweet -> {
            LocalDate tweetDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(tweet.getCreatedAt()));
            String tweetUrl = twitterUrlPrefix + tweet.getId();
            Set<Tag> tags = new HashSet<>();
            if (tweet.getHashtagEntities() != null) {
                for (HashtagEntity tag : tweet.getHashtagEntities()) {
                    if (!tag.getText().isEmpty()) {
                        tags.add(Tag.builder().content(tag.getText()).build());
                    }
                }
            }
            documentService.addDocument(tweetUrl, tweet.getText(), tweetDate, null, tags);
        });
    }
}