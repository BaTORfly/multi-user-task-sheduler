package io.github.batorfly.task_tracker_backend.service.jwt;

import io.github.batorfly.task_tracker_backend.domain.user.User;

import java.util.Date;
import java.util.Optional;

public interface JwtService {
    String generateAccessToken(final User user);
    String generateRefreshToken(final User user);
    Optional<String> getSubjectFromToken(final String token);
    Date getExpirationDateFromToken(final String token);
    String verifyToken(String token);
}
