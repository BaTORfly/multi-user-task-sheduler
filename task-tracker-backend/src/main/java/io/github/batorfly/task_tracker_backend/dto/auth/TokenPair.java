package io.github.batorfly.task_tracker_backend.dto.auth;

public record TokenPair (String accessToken, String refreshToken) {
}
