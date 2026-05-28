package com.krish.ai.interviewgenerator.config;

import com.krish.ai.interviewgenerator.constants.AppConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            @Value("${app.cache.questions-ttl-minutes:30}") long questionsTtlMinutes) {

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues();

        RedisCacheConfiguration questionCacheConfig = defaultConfig
                .entryTtl(Duration.ofMinutes(questionsTtlMinutes));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(Map.of(
                        AppConstants.Cache.QUESTIONS_CACHE, questionCacheConfig
                ))
                .build();
    }
}
