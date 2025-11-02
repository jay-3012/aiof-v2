package com.tech2nxt.aiofbackend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for exercise data
 * This caches frequently accessed data to reduce external API calls
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "exercises",      // Cache for exercise search results
                "exerciseDetails", // Cache for individual exercise details
                "categories",     // Cache for exercise categories
                "muscles",        // Cache for muscle data
                "equipment"       // Cache for equipment data
        );
    }
}