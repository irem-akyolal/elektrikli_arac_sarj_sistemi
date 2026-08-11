package com.proje.elektrikli_arac_sarj_sistemi.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.proje.elektrikli_arac_sarj_sistemi.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimitService {

    @Value("${rate-limit.capacity}")
    private int capacity;

    @Value("${rate-limit.duration-minutes}")
    private long durationMinutes;

    // Bir bucket, son erişimden 10 dakika sonra otomatik olarak bellekten silinir
    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(10))
            .maximumSize(100_000) // ekstra güvenlik: en fazla 100 bin farklı IP tutulsun
            .build();

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillGreedy(capacity, Duration.ofMinutes(durationMinutes))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket resolveBucket(String ipAddress) {
        return buckets.get(ipAddress, key -> createBucket());
    }

    public void consume(String ipAddress) {
        Bucket bucket = resolveBucket(ipAddress);

        if (!bucket.tryConsume(1)) {
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }
    }
}
