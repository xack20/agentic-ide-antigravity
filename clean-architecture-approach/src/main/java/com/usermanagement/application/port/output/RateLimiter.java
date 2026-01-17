package com.usermanagement.application.port.output;

/**
 * Output port for rate limiting operations.
 */
public interface RateLimiter {

    /**
     * Checks if an action is allowed and consumes a token if so.
     * 
     * @param key    The rate limit key (e.g., IP address + action)
     * @param action The action being rate limited
     * @return true if allowed, false if rate limited
     */
    boolean tryConsume(String key, String action);

    /**
     * Gets remaining attempts for a key/action.
     */
    long getRemainingAttempts(String key, String action);

    /**
     * Resets the rate limit for a key/action.
     */
    void reset(String key, String action);
}
