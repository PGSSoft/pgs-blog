package com.pgs.spark.bigdata.processor.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.reflect.ClassPath;
import com.pgs.spark.bigdata.processor.domain.Algorithm;
import com.pgs.spark.bigdata.processor.jobs.SparkJob;
import com.pgs.spark.bigdata.processor.jobs.tagClassificationJobs.BasicTagClassificationSparkJob;
import com.pgs.spark.bigdata.processor.jobs.tagClassificationJobs.TagClassificationSparkJob;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.jms.ConnectionFactory;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Optional;

/**
 * The App config.
 */
@Configuration
@EnableJms
public class AppConfig {

    @Value("${crossOrigin.origins}")
    private String[] allowedOrigins;

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    /**
     * Spark job names mapped to concrete jobs.
     *
     * @param context the context
     * @return the immutable map
     * @throws IOException
     */
    @Bean
    public ImmutableMap<String, SparkJob> sparkJobProvider(ApplicationContext context) throws IOException {
        ImmutableMap.Builder<String, SparkJob> builder = new Builder<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        ClassPath.from(loader).getTopLevelClassesRecursive(SparkJob.class.getPackage().getName()).stream()
                .forEach(classInfo -> {
                    final Class<?> aClass = classInfo.load();
                    if (!Modifier.isAbstract(aClass.getModifiers())
                            && SparkJob.class.isAssignableFrom(aClass)) {
                        final Object bean = context.getBean(aClass);
                        if (!(bean instanceof TagClassificationSparkJob))
                            builder.put(aClass.getSimpleName(), (SparkJob) bean);
                    }
                });
        return builder.build();
    }

    /**
     * Spark job algorithm provider immutable map.
     *
     * @param context the context
     * @return the immutable map
     * @throws IOException
     */
    @Bean(name = "algorithmsJobsMap")
    public ImmutableMap<Algorithm, SparkJob> sparkJobAlgorithmProvider(ApplicationContext context) throws IOException {
        ImmutableMap.Builder<Algorithm, SparkJob> builder = new Builder<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        ClassPath.from(loader).getTopLevelClassesRecursive(SparkJob.class.getPackage().getName()).stream()
                .forEach(classInfo -> {
                    final Class<?> aClass = classInfo.load();
                    if (!Modifier.isAbstract(aClass.getModifiers())
                            && SparkJob.class.isAssignableFrom(aClass)) {
                        final Object bean = context.getBean(aClass);
                        Optional<Algorithm> algorithm = Algorithm.fromValue(aClass.getSimpleName());
                        algorithm.ifPresent(consumableAlgorithm -> builder.put(consumableAlgorithm, (SparkJob) bean));
                    }
                });
        return builder.build();
    }

    /**
     * Cors filter configuration.
     *
     * @return the cors filter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        for (String allowedOrigin : allowedOrigins) {
            config.addAllowedOrigin(allowedOrigin);
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * JMS connection factory.
     *
     * @return the connection factory
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
        factory.setBrokerURL(brokerUrl);
        return factory;
    }

    /**
     * JMS container factory.
     *
     * @param connectionFactory
     * @return the jms listener container factory
     */
    @Bean
    public JmsListenerContainerFactory<?> myJmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
