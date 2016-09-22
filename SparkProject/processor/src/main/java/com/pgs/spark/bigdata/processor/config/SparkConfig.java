package com.pgs.spark.bigdata.processor.config;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.StreamingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Spark configuration.
 */
@Configuration
public class SparkConfig {

    @Value("${spark.application.name}")
    private String appName;

    @Value("${spark.master}")
    private String sparkMaster;

    @Value("${spring.data.cassandra.contactPoints}")
    private String cassandraHost;

    @Value("${spring.data.cassandra.keyspaceName}")
    private String cassandraKeyspace;

    /**
     * Spark-cassandra config.
     *
     * @return the spark conf
     */
    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setAppName(appName)
                .setMaster(sparkMaster)
                .set("spark.cassandra.connection.host", cassandraHost)
                .set("spark.cassandra.sql.keyspace", cassandraKeyspace);
    }

    /**
     * Gets spark context.
     *
     * @param sparkConf the spark conf
     * @return the spark context
     */
    @Bean
    public SparkContext getSparkContext(SparkConf sparkConf) {
        return new SparkContext(sparkConf);
    }

    /**
     * Gets sql context.
     *
     * @param javaSparkContext the java spark context
     * @return the sql context
     */
    @Bean
    @Autowired
    public SQLContext getSqlContext(SparkContext javaSparkContext) {
        return new SQLContext(javaSparkContext);
    }

    /**
     * Gets streaming context.
     *
     * @param sparkContext the spark context
     * @return the streaming context
     */
    @Bean
    public StreamingContext streamingContext(SparkContext sparkContext) {
        return new StreamingContext(sparkContext, Durations.seconds(5));
    }
}
