package io.github.batorfly.task_tracker_backend.service.jwt;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

public class JwtServiceImpl implements JwtService {
    private final int expireTimeForAccess;
    private final long expireTimeForRefresh;
    private final SecretKey secretKey;

    public JwtServiceImpl(
            @Value("${jwt.access.lifetime}") int expireTimeForAccess,
            @Value("jwt.refresh.lifetime") long expireTimeForRefresh,
            @Value("${jwt.secret}") SecretKey secretKey) {

        this.expireTimeForAccess = expireTimeForAccess;
        this.expireTimeForRefresh = expireTimeForRefresh;
        this.secretKey = secretKey;
    }

    @Override
    public String generateAccessToken(User user) {
        return "";
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
