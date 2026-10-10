package com.disasterresponse.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Standard JWT Utility for token generation, parsing, and claims extraction.
 */
public class JwtUtil {

    private static final String DEFAULT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_MS = 86400000L; // 24 hours

    private static Key getSigningKey() {
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.trim().isEmpty()) {
            secret = DEFAULT_SECRET;
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(Long id, String email, String role, String name) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("email", email)
                .claim("role", role)
                .claim("name", name)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getEmailFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public static Long getUserIdFromToken(String token) {
        Object idObj = parseToken(token).get("id");
        if (idObj instanceof Number) {
            return ((Number) idObj).longValue();
        }
        return Long.parseLong(idObj.toString());
    }

    public static String getRoleFromToken(String token) {
        Object roleObj = parseToken(token).get("role");
        return roleObj != null ? roleObj.toString() : null;
    }
}
