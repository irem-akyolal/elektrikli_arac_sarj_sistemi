package com.proje.elektrikli_arac_sarj_sistemi.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.proje.elektrikli_arac_sarj_sistemi.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;

import java.util.Map;


@Service
public class RateLimitService {


    @Value("${rate-limit.capacity}")
    private int capacity;

    @Value("${rate-limit.duration-minutes}")
    private long durationMinutes;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {

    Bandwidth limit = Bandwidth.builder()
            .capacity(capacity)
            .refillGreedy(capacity, Duration.ofMinutes(durationMinutes))
            .build();

    return Bucket.builder()
            .addLimit(limit)
            .build();
}

// eğer bucket yoksa oluşturuyoruz, varsa var olanı döndürüyoruz
private Bucket resolveBucket(String ipAddress) {

    return buckets.computeIfAbsent(
            ipAddress,
            key -> createBucket()
    );
}
// token sayısını kontrol eder ve eğer limit aşılmışsa RateLimitExceededException fırlatır
public void consume(String ipAddress) {

    Bucket bucket = resolveBucket(ipAddress);

    if (!bucket.tryConsume(1)) {
        throw new RateLimitExceededException(
                "Too many requests. Please try again later."
        );
    }
}

}
