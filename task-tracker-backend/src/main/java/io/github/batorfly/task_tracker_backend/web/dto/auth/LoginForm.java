package io.github.batorfly.task_tracker_backend.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginForm (
        @Schema(
                description = "User email address.",
                example = "john.smith@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email cannot be empty")
        String email,

        @Schema(
                description = "User password.",
                example = "StrongPass123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
