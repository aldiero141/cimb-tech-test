package com.cimb.callmonitoring.ratelimit;

public record RateLimitResult(boolean allowed, int retryAfterSeconds, int remaining) {

    public static RateLimitResult allowed(int remaining) {
        return new RateLimitResult(true, 0, remaining);
    }

    public static RateLimitResult denied(int retryAfterSeconds) {
        return new RateLimitResult(false, retryAfterSeconds, 0);
    }
}
