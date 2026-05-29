package io.github.batorfly.task_tracker_backend;

import lombok.NoArgsConstructor;

import java.util.Optional;

@NoArgsConstructor
public final class Utils {
    public static Optional<String> getTokenFromAuthHeader(final String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer "))
            return Optional.of(authHeader.substring(7));

        return Optional.empty();
    }
}
