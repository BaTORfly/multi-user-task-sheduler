package io.github.batorfly.task_tracker_scheduler.dto.email;

public record EmailCommand(String to, String header, String text) {
}
