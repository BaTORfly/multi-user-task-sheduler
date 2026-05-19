package io.github.batorfly.task_tracker_backend.web.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public record TaskDto(
        Long id,
        @NotBlank(message = "Title cannot be empty")
        String title,
        String description,
        @JsonProperty("done")
        boolean isDone,
        @JsonProperty("completion_time")
        Instant completionTime
) {
}
