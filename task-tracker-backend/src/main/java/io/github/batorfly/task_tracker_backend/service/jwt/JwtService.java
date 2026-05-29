package io.github.batorfly.task_tracker_backend.service.jwt;

import io.github.batorfly.task_tracker_backend.domain.user.User;

import java.util.Date;
import java.util.Optional;

public interface JwtService {
    String generateToken(User user);
    String generateRefreshToken(User user);
    Optional<String> getSubjectFromToken(String token);
    Date getExpirationDateFromToken(String token);
    String verifyToken(String token);
}
