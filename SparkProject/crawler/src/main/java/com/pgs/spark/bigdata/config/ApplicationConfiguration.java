package com.pgs.spark.bigdata.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import com.pgs.spark.bigdata.domain.SocialMedia;
import com.pgs.spark.bigdata.service.socialMediaCrawling.SocialMediaCrawlingService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The type Application configuration.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class ApplicationConfiguration {

    @Value("${crossOrigin.allowedOrigins}")
    private String[] allowedOrigins;

    @Value("#{'${crossOrigin.allowedMethods}'.split(',')}")
    private List<String> allowedMethods;

    /**
     * Social medias mapped to our services.
     *
     * @param context the context
     * @return the immutable map
     * @throws IOException the io exception
     */
    @Bean(name = "mediaServiceMap")
    public ImmutableMap<SocialMedia, SocialMediaCrawlingService> socialMediasWithServices(ApplicationContext context) throws IOException {
        ImmutableMap.Builder<SocialMedia, SocialMediaCrawlingService> builder = ImmutableMap.builder();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassPath.from(loader).getTopLevelClasses(SocialMediaCrawlingService.class.getPackage().getName())
                .forEach(classInfo -> {
                    final Class<?> aClass = classInfo.load();
                    if (SocialMediaCrawlingService.class.isAssignableFrom(aClass) && !SocialMediaCrawlingService.class.equals(aClass)) {
                        Optional<SocialMedia> socialMedia = SocialMedia.fromClass(aClass.getSimpleName());
                        socialMedia.ifPresent(media -> builder.put(media, (SocialMediaCrawlingService) context.getBean(aClass)));
                    }
                });

        return builder.build();
    }

    @Bean
    public OncePerRequestFilter corsFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
                response.addHeader("Access-Control-Allow-Origin", "*");
                if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    response.addHeader("Access-Control-Allow-Headers", "Content-Type");
                    response.addHeader("Access-Control-Max-Age", "1");
                }
                filterChain.doFilter(request, response);
            }
        };
    }
}
