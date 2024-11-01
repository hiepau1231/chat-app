package com.chatapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.support.AbstractMessageChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue")
              .setHeartbeatValue(new long[]{10000, 10000})
              .setTaskScheduler(heartBeatScheduler());
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024) // 512KB
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(512 * 1024) // 512KB
                   .setSendBufferSizeLimit(1024 * 1024) // 1MB
                   .setSendTimeLimit(20 * 1000) // 20 seconds
                   .setTimeToFirstMessage(60 * 1000); // 60 seconds
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        messageConverters.add(converter);
        return false;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor());
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor());
    }

    @Bean
    public org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler heartBeatScheduler() {
        org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler scheduler = 
            new org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        return scheduler;
    }

    @Bean
    public org.springframework.messaging.support.ChannelInterceptor channelInterceptor() {
        return new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(
                org.springframework.messaging.Message<?> message, 
                org.springframework.messaging.MessageChannel channel) {
                try {
                    // Add any message validation or transformation here
                    return message;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }
}
