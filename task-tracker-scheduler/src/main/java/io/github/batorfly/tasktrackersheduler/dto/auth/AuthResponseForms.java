package io.github.batorfly.tasktrackersheduler.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseForms (@JsonProperty("access_token") String accessToken) {
}
