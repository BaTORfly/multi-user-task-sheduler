package io.github.batorfly.task_tracker_scheduler.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(@JsonProperty("access_token") String accessToken) {
}
