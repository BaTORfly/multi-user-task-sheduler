package io.github.batorfly.task_tracker_backend.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseForm(@JsonProperty("access_token") String accessToken) {

}
