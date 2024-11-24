package Project.Final.FeedingTheNeeding.config;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Configuration
public class TokenService {

    private static final Logger logger = LogManager.getLogger(TokenService.class);

    @Value("${jwt.secret}")
    private String secret;

    private final long expirationTime = 1000 * 60 * 60 * 24; // 24 hours
    private SecretKey key;

    // Initialize the secret key securely
    @PostConstruct
    public void init() {
        // Generate a 256-bit key for HS256 algorithm
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email) {
        logger.info("start-generateToken. args: " + email);
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        logger.info("start-validateToken. args: " + token);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed: ", e);
            return false;
        }
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
