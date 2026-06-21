package io.github.batorfly.task_tracker_backend.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponseForm(
        @JsonProperty("access_token")
        @Schema(
                description = "JWT access token.",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLnNtaXRoQGV4YW1wbGUuY29tIn0.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
        )
        String accessToken
) {

}
