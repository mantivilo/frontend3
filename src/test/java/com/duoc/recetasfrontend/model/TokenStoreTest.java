package com.duoc.recetasfrontend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenStoreTest {

    @Test
    void testSetAndGetToken() {
        // Arrange
        TokenStore tokenStore = new TokenStore();
        String expectedToken = "sample-token";

        // Act
        tokenStore.setToken(expectedToken);

        // Assert
        assertEquals(expectedToken, tokenStore.getToken());
    }

    @Test
    void testTokenIsInitiallyNull() {
        // Arrange
        TokenStore tokenStore = new TokenStore();

        // Assert
        assertNull(tokenStore.getToken(), "The token should initially be null.");
    }

    @Test
    void testSetTokenToNull() {
        // Arrange
        TokenStore tokenStore = new TokenStore();
        tokenStore.setToken("sample-token");

        // Act
        tokenStore.setToken(null);

        // Assert
        assertNull(tokenStore.getToken(), "The token should be null after setting it to null.");
    }
}
