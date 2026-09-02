package com.cimb.callmonitoring.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "test-secret-key-that-is-at-least-32-bytes-long-for-hmac";
    private static final long EXPIRATION_MS = 3600_000L;

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, EXPIRATION_MS);
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
    }

    @Test
    void generatedTokenIsValidForSameUser() {
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("admin", jwtService.extractUsername(token));
    }

    @Test
    void tokenIsNotValidForDifferentUser() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("other");
        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void twoTokensForSameUserDiffer() {
        String token1 = jwtService.generateToken(userDetails);
        String token2 = jwtService.generateToken(userDetails);
        assertNotEquals(token1, token2);
    }
}