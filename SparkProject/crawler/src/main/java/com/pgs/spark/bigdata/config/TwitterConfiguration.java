package com.pgs.spark.bigdata.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * The type Twitter configuration.
 */
@Configuration
@PropertySource("classpath:twitter4j.properties")
public class TwitterConfiguration {

    @Value("${oauth.consumerKey}")
    private String consumerKey;

    @Value("${oauth.consumerSecret}")
    private String consumerSecret;

    @Value("${oauth.accessToken}")
    private String accessToken;

    @Value("${oauth.accessTokenSecret}")
    private String accessTokenSecret;

    /**
     * Twitter4j main class.
     *
     * @return the twitter
     */
    @Bean
    public Twitter twitter() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(consumerKey)
                .setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(accessToken)
                .setOAuthAccessTokenSecret(accessTokenSecret);

        TwitterFactory twitterFactory = new TwitterFactory(cb.build());
        return twitterFactory.getInstance();
    }

    /**
     * Twitter stream factory.
     *
     * @return the twitter stream factory
     */
    @Bean
    public TwitterStreamFactory twitterStreamFactory() {
        return new TwitterStreamFactory();
    }

    /**
     * Twitter stream.
     *
     * @param twitterStreamFactory factory
     * @return the twitter stream
     */
    @Bean
    public TwitterStream twitterStream(TwitterStreamFactory twitterStreamFactory) {
        return twitterStreamFactory.getInstance();
    }
}
