package at.bif.swen.rest.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long ttlSeconds;

    public JwtService(
            @Value("${security.jwt-secret}") String secret,
            @Value("${security.jwt-ttl-seconds:3600}") long ttlSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlSeconds = ttlSeconds;
    }

    public String generateToken(String username, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .addClaims(claims)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
