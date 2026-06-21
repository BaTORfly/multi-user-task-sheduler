package io.github.batorfly.task_tracker_backend.exception.auth;

public class AuthorizationFailedException extends RuntimeException{
    public AuthorizationFailedException(String message) {
        super(message);
    }

    public AuthorizationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationFailedException(Throwable cause) {
        super(cause);
    }
}
