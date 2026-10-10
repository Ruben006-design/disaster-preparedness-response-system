package com.disasterresponse;

import com.disasterresponse.enums.RoleType;
import com.disasterresponse.util.BCrypt;
import com.disasterresponse.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthAndSecurityTest {

    @Test
    @DisplayName("BCrypt successfully hashes and verifies plaintext passwords")
    public void testBCryptHashing() {
        String rawPassword = "StudentPass@123";
        String hash = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"));
        assertTrue(BCrypt.checkpw(rawPassword, hash));
        assertFalse(BCrypt.checkpw("WrongPassword@999", hash));
    }

    @Test
    @DisplayName("JwtUtil correctly generates tokens and extracts user claims")
    public void testJwtGenerationAndValidation() {
        Long userId = 42L;
        String email = "student.alex@university.edu";
        String role = RoleType.STUDENT.name();
        String name = "Alex Rivera";

        String token = JwtUtil.generateToken(userId, email, role, name);
        assertNotNull(token);
        assertTrue(JwtUtil.validateToken(token));

        assertEquals(email, JwtUtil.getEmailFromToken(token));
        assertEquals(userId, JwtUtil.getUserIdFromToken(token));
        assertEquals(role, JwtUtil.getRoleFromToken(token));
    }

    @Test
    @DisplayName("JwtUtil rejects invalid or tampered tokens")
    public void testInvalidToken() {
        assertFalse(JwtUtil.validateToken("invalid.token.structure"));
        assertFalse(JwtUtil.validateToken(""));
        assertFalse(JwtUtil.validateToken(null));
    }
}
