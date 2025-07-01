package com.jobPortal.Security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token-expiration}")
    private long tokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;


    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaim(token,claims->claims.get("role",String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    public String generateToken(String email,String role) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        return createToken(email,claims);
    }
    public String generateRefreshToken(String email,String role) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role);
        return createRefreshToken(email,claims);
    }

    private String createToken(String subject,Map<String,Object> claims) {
        return Jwts.builder().claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }
    private String createRefreshToken(String subject,Map<String,Object> claims) {
        return Jwts.builder().claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    public Boolean validateToken(String token, String email) {
        final String extractedUsername = extractEmail(token);
        return (extractedUsername.equals(email) && !isTokenExpired(token));
    }
}