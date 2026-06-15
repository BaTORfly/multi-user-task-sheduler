package io.github.batorfly.task_tracker_backend.exception.auth;

public class CookiesNotFoundException extends RuntimeException {
    public CookiesNotFoundException(String message) {
        super(message);
    }
}
