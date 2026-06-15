package io.github.batorfly.task_tracker_backend.web.dto.auth;

public record TokenPair (String accessToken, String refreshToken) {
}
