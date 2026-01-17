package com.usermanagement.infrastructure.service;

import com.usermanagement.application.port.output.RateLimiter;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of RateLimiter using Bucket4j.
 * For production, consider Redis-based implementation.
 */
@Service
public class InMemoryRateLimiter implements RateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final int registrationLimit;
    private final Duration registrationWindow;
    private final int profileUpdateLimit;
    private final Duration profileUpdateWindow;
    private final int adminSearchLimit;
    private final Duration adminSearchWindow;

    public InMemoryRateLimiter(
            @Value("${policy.rate-limit.registration.limit:5}") int registrationLimit,
            @Value("${policy.rate-limit.registration.window:15m}") Duration registrationWindow,
            @Value("${policy.rate-limit.profile-update.limit:10}") int profileUpdateLimit,
            @Value("${policy.rate-limit.profile-update.window:24h}") Duration profileUpdateWindow,
            @Value("${policy.rate-limit.admin-search.limit:100}") int adminSearchLimit,
            @Value("${policy.rate-limit.admin-search.window:1m}") Duration adminSearchWindow) {
        this.registrationLimit = registrationLimit;
        this.registrationWindow = registrationWindow;
        this.profileUpdateLimit = profileUpdateLimit;
        this.profileUpdateWindow = profileUpdateWindow;
        this.adminSearchLimit = adminSearchLimit;
        this.adminSearchWindow = adminSearchWindow;
    }

    @Override
    public boolean tryConsume(String key, String action) {
        String bucketKey = key + ":" + action;
        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> createBucket(action));
        return bucket.tryConsume(1);
    }

    @Override
    public long getRemainingAttempts(String key, String action) {
        String bucketKey = key + ":" + action;
        Bucket bucket = buckets.get(bucketKey);
        if (bucket == null) {
            return getLimit(action);
        }
        return bucket.getAvailableTokens();
    }

    @Override
    public void reset(String key, String action) {
        String bucketKey = key + ":" + action;
        buckets.remove(bucketKey);
    }

    private Bucket createBucket(String action) {
        int limit = getLimit(action);
        Duration window = getWindow(action);

        Bandwidth bandwidth = Bandwidth.classic(limit, Refill.intervally(limit, window));
        return Bucket.builder().addLimit(bandwidth).build();
    }

    private int getLimit(String action) {
        return switch (action) {
            case "registration" -> registrationLimit;
            case "profile-update" -> profileUpdateLimit;
            case "admin-search" -> adminSearchLimit;
            default -> 10; // Default limit
        };
    }

    private Duration getWindow(String action) {
        return switch (action) {
            case "registration" -> registrationWindow;
            case "profile-update" -> profileUpdateWindow;
            case "admin-search" -> adminSearchWindow;
            default -> Duration.ofMinutes(1);
        };
    }
}
