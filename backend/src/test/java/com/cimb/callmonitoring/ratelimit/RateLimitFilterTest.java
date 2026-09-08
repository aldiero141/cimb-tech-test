package com.cimb.callmonitoring.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

class RateLimitFilterTest {

    private RateLimitProperties props;
    private FixedWindowRateLimiter limiter;
    private RateLimitFilter filter;

    @BeforeEach
    void setUp() {
        props = new RateLimitProperties();
        props.setEnabled(true);
        props.setTrustXff(true);
        props.setRules(java.util.List.of(
                new RateLimitRule("/api/auth/login", 5, 60),
                new RateLimitRule("/api/calls", 60, 60)
        ));
        Clock clock = Clock.fixed(Instant.parse("2026-09-08T10:00:00Z"), ZoneId.of("UTC"));
        limiter = new FixedWindowRateLimiter(clock);
        IpKeyResolver resolver = new IpKeyResolver(props);
        filter = new RateLimitFilter(props, limiter, resolver);
    }

    @Test
    void allowsFirstFiveThenDeniesSixthWith429() throws Exception {
        for (int i = 0; i < 5; i++) {
            MockHttpServletRequest req = req("/api/auth/login", "1.2.3.4");
            MockHttpServletResponse res = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);
            filter.doFilterInternal(req, res, chain);
            assertEquals(200, res.getStatus() == 0 ? 200 : res.getStatus()); // mock response defaults 0 = not set; filter passed through
            verify(chain).doFilter(req, res);
        }
        MockHttpServletRequest req6 = req("/api/auth/login", "1.2.3.4");
        MockHttpServletResponse res6 = new MockHttpServletResponse();
        FilterChain chain6 = mock(FilterChain.class);
        filter.doFilterInternal(req6, res6, chain6);
        assertEquals(429, res6.getStatus());
        assertEquals("60", res6.getHeader("Retry-After"));
        String body = res6.getContentAsString();
        // body contains retryAfter
        assert body.contains("\"retryAfter\"") || body.contains("retryAfter");
        verify(chain6, never()).doFilter(req6, res6);
    }

    @Test
    void differentIpNotAffected() throws Exception {
        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(req("/api/auth/login", "1.2.3.4"), new MockHttpServletResponse(), mock(FilterChain.class));
        }
        MockHttpServletRequest req = req("/api/auth/login", "5.6.7.8");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilterInternal(req, res, chain);
        verify(chain).doFilter(req, res);
    }

    @Test
    void differentEndpointIsolated() throws Exception {
        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(req("/api/auth/login", "1.2.3.4"), new MockHttpServletResponse(), mock(FilterChain.class));
        }
        MockHttpServletRequest req = req("/api/calls", "1.2.3.4");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilterInternal(req, res, chain);
        verify(chain).doFilter(req, res);
    }

    @Test
    void actuatorBypasses() throws Exception {
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = req("/actuator/health", "1.2.3.4");
            MockHttpServletResponse res = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);
            filter.doFilterInternal(req, res, chain);
            verify(chain).doFilter(req, res);
        }
    }

    @Test
    void disabledPassesAll() throws Exception {
        props.setEnabled(false);
        for (int i = 0; i < 10; i++) {
            MockHttpServletRequest req = req("/api/auth/login", "1.2.3.4");
            MockHttpServletResponse res = new MockHttpServletResponse();
            FilterChain chain = mock(FilterChain.class);
            filter.doFilterInternal(req, res, chain);
            verify(chain).doFilter(req, res);
        }
    }

    @Test
    void xffFallbackToRemoteAddr() throws Exception {
        // No XFF header -> uses remoteAddr
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/auth/login");
        req.setRemoteAddr("9.9.9.9");
        MockHttpServletResponse res = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilterInternal(req, res, chain);
        verify(chain).doFilter(req, res);

        // With XFF containing multiple ips, first is used
        MockHttpServletRequest req2 = new MockHttpServletRequest("POST", "/api/auth/login");
        req2.setRemoteAddr("9.9.9.9");
        req2.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2");
        MockHttpServletResponse res2 = new MockHttpServletResponse();
        FilterChain chain2 = mock(FilterChain.class);
        filter.doFilterInternal(req2, res2, chain2);
        verify(chain2).doFilter(req2, res2);
        // 1.1.1.1 vs 9.9.9.9 are different keys, so both should have counts 1 isolated; verify by filling 1.1.1.1 to limit
        for (int i = 0; i < 4; i++) {
            MockHttpServletRequest r = new MockHttpServletRequest("POST", "/api/auth/login");
            r.addHeader("X-Forwarded-For", "1.1.1.1, 2.2.2.2");
            filter.doFilterInternal(r, new MockHttpServletResponse(), mock(FilterChain.class));
        }
        MockHttpServletRequest rDenied = new MockHttpServletRequest("POST", "/api/auth/login");
        rDenied.addHeader("X-Forwarded-For", "1.1.1.1");
        MockHttpServletResponse resDenied = new MockHttpServletResponse();
        FilterChain chainDenied = mock(FilterChain.class);
        filter.doFilterInternal(rDenied, resDenied, chainDenied);
        assertEquals(429, resDenied.getStatus());
    }

    private MockHttpServletRequest req(String uri, String xffIp) {
        MockHttpServletRequest r = new MockHttpServletRequest("GET", uri);
        r.addHeader("X-Forwarded-For", xffIp);
        r.setRemoteAddr("10.0.0.1");
        return r;
    }
}
