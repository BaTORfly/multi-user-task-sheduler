package io.github.batorfly.task_tracker_backend.service.jwt;

import io.github.batorfly.task_tracker_backend.domain.user.User;

import java.util.Date;
import java.util.Optional;

public class JwtServiceImpl implements JwtService {
    @Override
    public String generateToken(User user) {
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
