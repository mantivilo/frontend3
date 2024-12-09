package com.duoc.recetasfrontend.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BannerTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        String expectedNombre = "Sample Banner";
        String expectedImagenUrl = "http://example.com/image.jpg";
        String expectedEnlaceUrl = "http://example.com";

        // Act
        Banner banner = new Banner(expectedNombre, expectedImagenUrl, expectedEnlaceUrl);

        // Assert
        assertEquals(expectedNombre, banner.getNombre());
        assertEquals(expectedImagenUrl, banner.getImagenUrl());
        assertEquals(expectedEnlaceUrl, banner.getEnlaceUrl());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Banner banner = new Banner("Initial Name", "http://initial.com/image.jpg", "http://initial.com");

        // Act
        banner.setNombre("Updated Banner");
        banner.setImagenUrl("http://updated.com/image.jpg");
        banner.setEnlaceUrl("http://updated.com");

        // Assert
        assertEquals("Updated Banner", banner.getNombre());
        assertEquals("http://updated.com/image.jpg", banner.getImagenUrl());
        assertEquals("http://updated.com", banner.getEnlaceUrl());
    }

    @Test
    void testEmptyConstructor() {
        // Arrange & Act
        Banner banner = new Banner(null, null, null);

        // Assert
        assertNull(banner.getNombre());
        assertNull(banner.getImagenUrl());
        assertNull(banner.getEnlaceUrl());
    }
}
