package io.github.batorfly.task_tracker_scheduler.dto.task;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record TaskDto(
        Long id,
        String title,
        String description,
        @JsonProperty("done")
        boolean done,
        @JsonAlias({"completionTime", "completion_time"})
        Instant completionTime
) {
}
