package io.github.batorfly.task_tracker_scheduler.exception;

public class EmailPublishingException extends RuntimeException {
    public EmailPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
