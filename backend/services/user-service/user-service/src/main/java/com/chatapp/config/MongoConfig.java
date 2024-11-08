package com.chatapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.domain.Sort;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.chatapp.model.User;

@Slf4j
@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;
    private final MongoMappingContext mongoMappingContext;

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(
            LocalValidatorFactoryBean factory) {
        return new ValidatingMongoEventListener(factory);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @PostConstruct
    public void initIndexes() {
        log.info("Initializing MongoDB indexes...");
        
        try {
            IndexOperations indexOps = mongoTemplate.indexOps(User.class);
            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
            
            resolver.resolveIndexFor(User.class).forEach(indexOps::ensureIndex);
            
            indexOps.ensureIndex(new Index()
                .on("email", Sort.Direction.ASC)
                .unique()
                .sparse());
            
            indexOps.ensureIndex(new Index()
                .on("username", Sort.Direction.ASC)
                .unique()
                .sparse());
            
            indexOps.ensureIndex(new Index()
                .on("status", Sort.Direction.ASC));
            
            log.info("MongoDB indexes initialized successfully");
        } catch (Exception e) {
            log.error("Error initializing MongoDB indexes", e);
            throw new RuntimeException("Failed to initialize MongoDB indexes", e);
        }
    }
}
