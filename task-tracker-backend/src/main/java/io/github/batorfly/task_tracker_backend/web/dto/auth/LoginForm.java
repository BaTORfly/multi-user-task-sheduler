package io.github.batorfly.task_tracker_backend.web.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginForm (
        @NotBlank(message = "Email cannot be empty") String email,
        @NotBlank(message = "Password cannot be empty") String password
) {
}
