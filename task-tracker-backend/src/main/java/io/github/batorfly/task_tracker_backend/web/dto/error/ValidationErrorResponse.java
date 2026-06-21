package io.github.batorfly.task_tracker_backend.web.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ValidationErrorResponse(
        @Schema(
                description = "Validation errors by field name.",
                example = """
                        {
                          "firstName": "First name must be between 3 and 64 characters",
                          "lastName": "Last name must include only letters",
                          "email": "Invalid email",
                          "password": "Password must contain at least one uppercase and one lowercase letter"
                        }
                        """
        )
        Map<String, String> errorFields,

        @Schema(description = "Error timestamp in milliseconds since epoch.", example = "1782045600000")
        long timestamp
) {
}
