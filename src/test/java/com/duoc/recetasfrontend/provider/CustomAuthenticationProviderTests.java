package com.duoc.recetasfrontend.provider;

import com.duoc.recetasfrontend.model.TokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CustomAuthenticationProviderTests {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private TokenStore tokenStore;

    @InjectMocks
    private CustomAuthenticationProvider customAuthenticationProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customAuthenticationProvider = new CustomAuthenticationProvider(tokenStore, restTemplate);
    }

    @Test
    void testAuthenticate_Success() {
        // Mock the server response
        ResponseEntity<String> mockResponse = new ResponseEntity<>("mock-token", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        // Input authentication
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");

        // Execute the method
        Authentication result = customAuthenticationProvider.authenticate(auth);

        // Validate the result
        assertNotNull(result);
        assertEquals("user", result.getName());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(tokenStore).setToken("mock-token");
    }

    @Test
    void testAuthenticate_BadCredentials() {
        // Mock the server response with error status
        ResponseEntity<String> mockResponse = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class))).thenReturn(mockResponse);

        // Input authentication
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "wrong-password");

        // Expect exception
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            customAuthenticationProvider.authenticate(auth);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testAuthenticate_ServerError() {
        // Simulate a server error
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Server is down"));

        // Input authentication
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");

        // Expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customAuthenticationProvider.authenticate(auth);
        });

        assertEquals("Server is down", exception.getMessage());
    }

    @Test
    void testSupports_ValidClass() {
        assertTrue(customAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testSupports_InvalidClass() {
        assertFalse(customAuthenticationProvider.supports(String.class));
    }
}