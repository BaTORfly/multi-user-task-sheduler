package io.github.batorfly.tasktrackersheduler.dto.task;

import java.sql.Timestamp;

public record TaskDto(Long id, String title, String description, boolean isDone, Timestamp completionTime) {

}
