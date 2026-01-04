package com.practice.config;

import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;

    // HS256 = HMAC + SHA-256 > SHA-256 -> 256bit 키 요구 > 256bit = 32 bytes 보다 짧으면 보안 취약 or 일부 환경에서 런타임 예외
    @PostConstruct
    public void validateSecretKey() {
        byte[] key = jwtProperties.getSecret().getBytes();

        if (key.length < 32) {
            throw new IllegalStateException("JWT Secret key must be at least 32 bytes (256 bits) for HS256");
        }
    }

    public byte[] getSecret() {
        return jwtProperties.getSecret().getBytes();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(getSecret()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public String generateAccessToken(String userId, String role) {
        Date now = new Date();
        // Date expiry = new Date(now.getTime() + 1000 * 60 * 15); // 15분
        Date expiry = new Date(now.getTime() + 1000 * 10); // 10초

        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(
                        Keys.hmacShaKeyFor(getSecret()),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000L * 60 * 60 * 24 * 14); // 14일
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(Keys.hmacShaKeyFor(getSecret()), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
}
