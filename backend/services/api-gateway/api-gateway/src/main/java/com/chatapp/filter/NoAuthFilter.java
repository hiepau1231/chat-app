package com.chatapp.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class NoAuthFilter extends AbstractGatewayFilterFactory<NoAuthFilter.Config> {

    public static class Config {
        // empty class as we don't need any configuration
    }

    public NoAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange);
    }
}