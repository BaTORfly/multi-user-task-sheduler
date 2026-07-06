package io.github.batorfly.task_tracker_scheduler.exception;

public class BackendClientException extends RuntimeException {
    public BackendClientException(String message) {
        super(message);
    }

    public BackendClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
