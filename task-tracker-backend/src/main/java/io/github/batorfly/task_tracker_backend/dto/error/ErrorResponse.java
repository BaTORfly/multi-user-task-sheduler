package io.github.batorfly.task_tracker_backend.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "Error message.", example = "User already exists")
        String message,

        @Schema(description = "Error timestamp in milliseconds since epoch.", example = "1782045600000")
        long timestamp
) {
}
