package com.cimb.callmonitoring.ratelimit;

public record RateLimitRule(String path, int limit, int windowSeconds) {

    public long windowMs() {
        return (long) windowSeconds * 1000L;
    }
}
