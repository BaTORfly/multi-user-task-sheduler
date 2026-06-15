package io.github.batorfly.task_tracker_backend.service.jwt;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * JwtService version 1.0
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {
    private final int accessLifetime;
    private final long refreshLifetime;
    private final SecretKey signingKey;

    public JwtServiceImpl(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access.lifetime}") int accessLifetime,
            @Value("${jwt.refresh.lifetime}") long refreshLifetime
    ) {
        this.accessLifetime = accessLifetime;
        this.refreshLifetime = refreshLifetime;
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(final User user) {
        Claims claims = Jwts.claims()
                .subject(user.getEmail())
                .add("id", user.getId())
                .add("roles", user.getAuthorities())
                .build();

        Instant validity = Instant.now()
                .plus(this.accessLifetime, ChronoUnit.SECONDS);

        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        Claims claims = Jwts.claims()
                .subject(user.getEmail())
                .add("id", user.getId())
                .build();

        Instant validity = Instant.now()
                .plus(this.refreshLifetime, ChronoUnit.SECONDS);

        return Jwts.builder()
                .claims(claims)
                .expiration(Date.from(validity))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public Optional<String> getSubjectFromToken(String token) {
        return Optional.ofNullable(getClaim(token, Claims::getSubject));
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    @Override
    public String verifyToken(String refreshToken) {
        return getSubjectFromToken(refreshToken)
                .orElseThrow(() -> {
                    log.error("Token does not have subject (user email)");
                    return new MalformedJwtException("Token does not have subject (user email)");
                });
    }

    private <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
