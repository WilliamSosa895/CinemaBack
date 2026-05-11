package com.Esteban.cinema.Configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "12345678901234567890123456789012");
    }

    @Test
    void generateToken_extractClaims_andValidate() {
        String token = jwtService.generateToken("user@test.com", 42L, "USER");

        assertNotNull(token);
        assertEquals("user@test.com", jwtService.extractUsername(token));
        assertEquals(42L, jwtService.extractUserId(token));
        assertEquals("USER", jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token, "user@test.com"));
    }

    @Test
    void isTokenValid_returnsFalse_forDifferentUsername() {
        String token = jwtService.generateToken("user@test.com", 42L, "USER");

        assertFalse(jwtService.isTokenValid(token, "other@test.com"));
    }
}
