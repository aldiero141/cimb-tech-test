package com.cimb.callmonitoring.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitProperties properties;
    private final FixedWindowRateLimiter limiter;
    private final IpKeyResolver ipResolver;
    private final AntPathMatcher matcher = new AntPathMatcher();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RateLimitFilter(RateLimitProperties properties,
                           FixedWindowRateLimiter limiter,
                           IpKeyResolver ipResolver) {
        this.properties = properties;
        this.limiter = limiter;
        this.ipResolver = ipResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        // Exempt actuator unconditionally
        if (uri.startsWith("/actuator/")) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitRule matched = findMatchingRule(uri, method);
        if (matched == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = ipResolver.resolve(request);
        String compositeKey = ip + "|" + matched.path();

        RateLimitResult result = limiter.tryAcquire(compositeKey, matched);
        if (result.allowed()) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(result.retryAfterSeconds()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 429);
        body.put("error", "Too Many Requests");
        body.put("message", "Rate limit exceeded. Try again in " + result.retryAfterSeconds() + "s.");
        body.put("retryAfter", result.retryAfterSeconds());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private RateLimitRule findMatchingRule(String uri, String method) {
        for (RateLimitRule rule : properties.getRules()) {
            String pattern = rule.path();
            // Support method-agnostic matching; pattern is pure path
            // For login rule we want exact /api/auth/login; for calls we match prefix
            if (matcher.match(pattern, uri)) {
                return rule;
            }
            // Also support matching with strict equality fallback
            if (pattern.equals(uri)) {
                return rule;
            }
        }
        return null;
    }
}
