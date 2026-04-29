package com.bmu1093a.quill.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String SECRET = "mySuperSecretKeymySuperSecretKey123456"; // must be long enough
    private final long EXPIRATION = 1000 * 60; // 1 minute
    private final long REFRESH_EXPIRATION = 1000 * 60 * 5; // 5 minutes

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtUtil, "refreshExpiration", REFRESH_EXPIRATION);
    }

    // ✅ 1. Generate and validate access token
    @Test
    void shouldGenerateValidToken() {
        String token = jwtUtil.generateToken("test@mail.com", "USER");

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
    }

    // ✅ 2. Extract email
    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtUtil.generateToken("test@mail.com", "USER");

        String email = jwtUtil.getEmailFromToken(token);

        assertEquals("test@mail.com", email);
    }

    // ✅ 3. Extract role
    @Test
    void shouldExtractRoleFromToken() {
        String token = jwtUtil.generateToken("test@mail.com", "ADMIN");

        String role = jwtUtil.getRoleFromToken(token);

        assertEquals("ADMIN", role);
    }

    // ✅ 4. Extract expiration
    @Test
    void shouldExtractExpiration() {
        String token = jwtUtil.generateToken("test@mail.com", "USER");

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    // ❗ 5. Expired token should be invalid
    @Test
    void shouldReturnFalse_whenTokenExpired() throws InterruptedException {
        // set very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L);

        String token = jwtUtil.generateToken("test@mail.com", "USER");

        Thread.sleep(5); // let it expire

        assertFalse(jwtUtil.isTokenValid(token));
    }

    // ❗ 6. Invalid token (tampered)
    @Test
    void shouldReturnFalse_whenTokenIsInvalid() {
        String token = jwtUtil.generateToken("test@mail.com", "USER");

        // tamper token
        String invalidToken = token + "corrupted";

        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }

    // ✅ 7. Refresh token generation
    @Test
    void shouldGenerateValidRefreshToken() {
        String token = jwtUtil.generateRefreshToken("test@mail.com", "USER");

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
    }
}