package com.pgs.spark.bigdata.service.socialMediaCrawling;

import com.pgs.spark.bigdata.service.DocumentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TwitterCrawlingServiceTest {

    private final static String twitterUrlPrefix = "https://twitter.com/statuses/";

    @InjectMocks
    private TwitterCrawlingService twitterCrawlingService = new TwitterCrawlingService();

    @Mock
    private Twitter twitter;

    @Mock
    private StatusListener statusListener;

    @Mock
    private TwitterStream twitterStream;

    @Mock
    private DocumentService documentService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCrawlTwitter() throws TwitterException {
        //given
        Set<String> keywords = new HashSet<>(Arrays.asList("Duda", "Tusk", "Komorowski"));
        Query query = getQueryFromKeywords(keywords);
        FilterQuery tweetFilterQuery = new FilterQuery(keywords.toArray(new String[keywords.size()]));
        tweetFilterQuery.language("pl");
        QueryResult queryResult = mock(QueryResult.class);
        Status status0 = mock(Status.class);
        Status status1 = mock(Status.class);

        Calendar cal = Calendar.getInstance();
        Date status0Date = cal.getTime();

        cal.add(Calendar.MONTH, 1);
        Date status1Date = cal.getTime();

        when(twitter.search(getQueryFromKeywords(keywords))).thenReturn(queryResult);
        when(queryResult.getTweets()).thenReturn(Arrays.asList(status0, status1));
        when(status0.getId()).thenReturn(0L);
        when(status0.getText()).thenReturn("status0");
        when(status0.getCreatedAt()).thenReturn(status0Date);
        when(status1.getId()).thenReturn(1L);
        when(status1.getText()).thenReturn("status1");
        when(status1.getCreatedAt()).thenReturn(status1Date);
        String status0Url = twitterUrlPrefix + status0.getId();
        String status1Url = twitterUrlPrefix + status1.getId();
        LocalDate status0LocalDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(status0.getCreatedAt()));
        LocalDate status1LocalDate = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(status1.getCreatedAt()));

        //when
        twitterCrawlingService.crawl(keywords);

        //then
        verify(twitter).search(query);
        verify(twitterStream).addListener(statusListener);
        verify(twitterStream).filter(tweetFilterQuery);
        verify(documentService).addDocument(status0Url, status0.getText(), status0LocalDate, null, Collections.emptySet());
        verify(documentService).addDocument(status1Url, status1.getText(), status1LocalDate, null, Collections.emptySet());

    }

    private Query getQueryFromKeywords(final Set<String> keywords) {
        StringJoiner stringJoiner = new StringJoiner(" OR ");
        keywords.forEach(stringJoiner::add);
        Query query = new Query(stringJoiner.toString());
        query.setLang("pl");
        query.setCount(100);
        return query;
    }

}
