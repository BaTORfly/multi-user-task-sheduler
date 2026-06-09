package io.github.batorfly.task_tracker_backend.service.jwt;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

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
        
    }

    @Override
    public String generateRefreshToken(User user) {
        return "";
    }

    @Override
    public Optional<String> getSubjectFromToken(String token) {
        return Optional.empty();
    }

    @Override
    public Date getExpirationDateFromToken(String token) {
        return null;
    }

    @Override
    public String verifyToken(String token) {
        return "";
    }
}
