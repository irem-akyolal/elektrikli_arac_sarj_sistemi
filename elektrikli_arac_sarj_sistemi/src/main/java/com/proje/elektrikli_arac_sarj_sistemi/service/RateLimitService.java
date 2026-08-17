package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.proje.elektrikli_arac_sarj_sistemi.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(100_000)
            .build();


    /**
     * Belirtilen IP ve endpoint için 1 istek tüketir.
     */
    public void consume(
            String ipAddress,
            String endpointKey,
            int capacity,
            long durationMinutes) {

        String bucketKey = ipAddress + ":" + endpointKey;

        Bucket bucket = buckets.get(
                bucketKey,
                key -> createBucket(capacity, durationMinutes)
        );

        if (!bucket.tryConsume(1)) {

            throw new RateLimitExceededException(
                    "Too many requests. Please try again later."
            );
        }
    }


    /**
     * Bucket oluşturur.
     */
    private Bucket createBucket(
            int capacity,
            long durationMinutes) {

        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(
                        capacity,
                        Duration.ofMinutes(durationMinutes)
                )
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}