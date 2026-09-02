package com.durel.bibliotheque.security;

import com.durel.bibliotheque.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.time.Instant;
import java.util.Date;

/**
 * Creates and validates JWT access tokens.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes =
                Decoders.BASE64.decode(secret);

        this.signingKey =
                Keys.hmacShaKeyFor(keyBytes);

        this.expirationMs = expirationMs;
    }

    /**
     * Creates a signed JWT identifying the authenticated user.
     */
    public String generateToken(User user) {

        Instant now = Instant.now();
        Instant expiration =
                now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extracts the user ID stored in the JWT subject.
     */
    public Long extractUserId(String token) {

        Claims claims = extractClaims(token);

        return Long.valueOf(claims.getSubject());
    }

    /**
     * Returns true only when the token has a valid signature
     * and has not expired.
     */
    public boolean isTokenValid(String token) {

        try {
            extractClaims(token);
            return true;

        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    /**
     * Verifies the JWT signature before returning its claims.
     */
    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
