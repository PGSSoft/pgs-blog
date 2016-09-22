package com.pgs.spark.bigdata;

import com.pgs.spark.bigdata.service.CrawlerService;
import com.pgs.spark.bigdata.service.fileImport.FileImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.net.URI;

/**
 * The type Application.
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = Application.class)
@PropertySource("classpath:application.properties")
public class Application implements CommandLineRunner {

    @Autowired
    private CrawlerService crawlerService;

    @Value("${crawlingOnStart}")
    private boolean crawlingOnStart;

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        if(crawlingOnStart) {
            crawlerService.crawl();
        }
    }
}
