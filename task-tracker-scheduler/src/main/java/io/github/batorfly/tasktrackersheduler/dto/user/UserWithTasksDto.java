package io.github.batorfly.tasktrackersheduler.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.batorfly.tasktrackersheduler.dto.task.TaskDto;

import java.util.Set;

public record UserWithTasksDto(Long id,
                               String email,
                               @JsonProperty("first_name") String firstName,
                               @JsonProperty("last_name") String lastName,
                               Set<TaskDto> tasks) {
}
