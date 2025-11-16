package org.example.sang_garden.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.sang_garden.auth.CustomUserDetails;
import org.example.sang_garden.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-minutes}")
    private Long expirationMinutes;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    @Value("${jwt.clock-skew-seconds}")
    private Long clockSkewSeconds;

    private Key signingKey;
    private JwtParser jwtParser;

    @PostConstruct
    void init() {
        // HS256 key should be at least 256 bits (~32 bytes). Prefer 64+ chars of random.
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parserBuilder()
                .requireIssuer(issuer)                 // enforce iss
                .requireAudience(audience)             // enforce aud
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .setSigningKey(signingKey)
                .build();
    }

    public String generateAccessToken(CustomUserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getUser().getUserRole());
        return createToken(claims, user.getUsername());
    }
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getUserRole());
        return createToken(claims, user.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(issuer)
                .setAudience(audience)
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirationMinutes*60)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public Claims validateAndGetClaims(String token) throws JwtException {
        /*
            IF TOKEN IS INVALID THEN THE LINE BELOW WILL THROW EXCEPTIONS
         */
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        Claims c = jws.getBody();

        // Defensive checks
        if (c.getSubject() == null || c.getExpiration() == null) {
            throw new JwtException("Missing sub/exp");
        }
//
//        // Optional: custom revocation (e.g., Redis/DB blacklist by jti)
//        String jti = c.getId();
//        if (jti != null && isRevoked(jti)) {
//            throw new JwtException("Token revoked");
//        }
        return c;
    }
}
