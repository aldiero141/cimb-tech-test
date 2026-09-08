package com.cimb.callmonitoring.ratelimit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class RateLimitConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
