package com.cimb.callmonitoring.ratelimit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Deep module: small interface hides all window calculation, storage and cleanup.
 * Seam is this class boundary. Thread-safe via ConcurrentHashMap.compute.
 */
@Component
public class FixedWindowRateLimiter {

    private final Clock clock;
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(Clock clock) {
        this.clock = clock;
    }

    /**
     * Attempt to consume one token for the given composite key and rule.
     *
     * @param compositeKey  e.g. "1.2.3.4|/api/auth/login"
     * @param rule          limit + window
     * @return allowed or denied with retryAfterSeconds
     */
    public RateLimitResult tryAcquire(String compositeKey, RateLimitRule rule) {
        long nowMs = clock.millis();
        long windowMs = rule.windowMs();
        WindowResult result = new WindowResult();

        windows.compute(compositeKey, (key, existing) -> {
            if (existing == null || nowMs - existing.windowStartMs >= windowMs) {
                // New window
                Window next = new Window(nowMs, 1);
                result.allowed = true;
                result.remaining = rule.limit() - 1;
                result.retryAfter = 0;
                return next;
            }
            if (existing.count < rule.limit()) {
                existing.count++;
                result.allowed = true;
                result.remaining = rule.limit() - existing.count;
                result.retryAfter = 0;
                return existing;
            }
            // Denied
            long retryMs = (existing.windowStartMs + windowMs) - nowMs;
            int retrySec = (int) Math.ceil(retryMs / 1000.0);
            if (retrySec < 1) {
                retrySec = 1;
            }
            result.allowed = false;
            result.remaining = 0;
            result.retryAfter = retrySec;
            return existing;
        });

        if (result.allowed) {
            return RateLimitResult.allowed(result.remaining);
        }
        return RateLimitResult.denied(result.retryAfter);
    }

    @Scheduled(fixedDelay = 300_000)
    public void purgeStale() {
        long nowMs = clock.millis();
        windows.entrySet().removeIf(entry -> {
            Window w = entry.getValue();
            // Remove if window expired more than one window ago (or 5 min for safety)
            // We don't have rule here, so use conservative max: 60s window => 120s stale
            // Simpler: remove if now - windowStart > 300_000 (5 min)
            return nowMs - w.windowStartMs > 300_000L;
        });
    }

    // Visible for tests
    int size() {
        return windows.size();
    }

    void clear() {
        windows.clear();
    }

    static class Window {
        long windowStartMs;
        int count;

        Window(long windowStartMs, int count) {
            this.windowStartMs = windowStartMs;
            this.count = count;
        }
    }

    private static class WindowResult {
        boolean allowed;
        int remaining;
        int retryAfter;
    }
}
