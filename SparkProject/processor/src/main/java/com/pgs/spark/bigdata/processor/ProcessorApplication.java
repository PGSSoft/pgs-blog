package com.pgs.spark.bigdata.processor;

import com.pgs.spark.bigdata.processor.domain.MultilayerPerceptronClassifierProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * The type Processor application.
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = ProcessorApplication.class)
@EnableConfigurationProperties({MultilayerPerceptronClassifierProperties.class})
public class ProcessorApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorApplication.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ProcessorApplication.class, args);
    }

}
