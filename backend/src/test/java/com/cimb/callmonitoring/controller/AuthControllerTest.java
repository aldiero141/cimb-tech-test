package com.cimb.callmonitoring.controller;

import com.cimb.callmonitoring.dto.LoginRequest;
import com.cimb.callmonitoring.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private AuthenticationManager authenticationManager;
    private UserDetailsService userDetailsService;
    private JwtService jwtService;
    private AuthController controller;

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtService = mock(JwtService.class);
        controller = new AuthController(authenticationManager, userDetailsService, jwtService);
    }

    @Test
    void loginSuccessfulReturnsToken() {
        LoginRequest request = new LoginRequest("admin", "password");
        UserDetails userDetails = new User("admin", "password", Collections.emptyList());

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = controller.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void loginInvalidCredentialsReturnsUnauthorized() {
        LoginRequest request = new LoginRequest("admin", "wrong");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid"));

        ResponseEntity<?> response = controller.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void logoutReturnsOk() {
        ResponseEntity<?> response = controller.logout();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
