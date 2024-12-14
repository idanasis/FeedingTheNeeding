package Project.Final.FeedingTheNeeding.Authentication.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtTokenServiceTest {

    @Mock
    private UserDetails mockUserDetails;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private Key secretKey;
    private String base64SecretKey;
    private final long jwtExpiration = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Generate secure key and configure JwtTokenService
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        base64SecretKey = java.util.Base64.getEncoder().encodeToString(secretKey.getEncoded());

        jwtTokenService = new JwtTokenService();
        setPrivateField(jwtTokenService, "secretKey", base64SecretKey);
        setPrivateField(jwtTokenService, "jwtExpiration", jwtExpiration);

        when(mockUserDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void testGenerateToken() {
        String token = jwtTokenService.generateToken(mockUserDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals("testuser", jwtTokenService.extractUsername(token));

        verify(mockUserDetails, atLeastOnce()).getUsername();
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtTokenService.generateToken(mockUserDetails);

        boolean isValid = jwtTokenService.isTokenValid(token, mockUserDetails);
        assertTrue(isValid);

        verify(mockUserDetails, atLeastOnce()).getUsername();
    }

    @Test
    void testExtractClaim() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("testuser")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        String role = jwtTokenService.extractClaim(token, claimsMap -> claimsMap.get("role").toString());
        assertEquals("ADMIN", role);
    }

    @Test
    void testGenerateTokenWithClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtTokenService.generateToken(claims, mockUserDetails);
        assertNotNull(token);

        String role = jwtTokenService.extractClaim(token, claimsMap -> claimsMap.get("role").toString());
        assertEquals("USER", role);
    }

    @Test
    void testIsTokenValid_NullToken() {
        assertFalse(jwtTokenService.isTokenValid(null, mockUserDetails));
    }

    @Test
    void testIsTokenValid_NullUserDetails() {
        String token = jwtTokenService.generateToken(mockUserDetails);
        assertFalse(jwtTokenService.isTokenValid(token, null));
    }

    @Test
    void testExtractUsername() {
        String token = jwtTokenService.generateToken(mockUserDetails);
        assertEquals("testuser", jwtTokenService.extractUsername(token));
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
