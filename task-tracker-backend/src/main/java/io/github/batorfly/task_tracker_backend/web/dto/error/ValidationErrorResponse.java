package io.github.batorfly.task_tracker_backend.web.dto.error;

import java.util.Map;

public record ValidationErrorResponse(Map<String, String> errorFields, long timestamp) {
}
