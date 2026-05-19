package io.github.batorfly.task_tracker_backend.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserWithoutTasksDto(
        Long id,
        String email,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName
) {
}
