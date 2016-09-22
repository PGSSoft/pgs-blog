package com.pgs.spark.bigdata.algorithmComparator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@PropertySource("classpath:application.properties")
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Value("${spring.websocket.broker}")
    private String broker;

    @Value("${spring.websocket.destinationPrefixes}")
    private String destinationPrefixes;

    @Value("#{'${spring.websocket.endpoints}'.split(',')}")
    private List<String> endpoints;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(broker);
        config.setApplicationDestinationPrefixes(destinationPrefixes);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        endpoints.forEach(e -> registry.addEndpoint(e).setAllowedOrigins("*").withSockJS());
    }

}
