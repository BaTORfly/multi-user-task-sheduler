package io.github.batorfly.task_tracker_scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "backend")
public record BackendClientProperties(
        String serviceName,
        String authPath,
        String usersPath
) {
    public BackendClientProperties {
        if (serviceName == null || serviceName.isBlank()) {
            serviceName = "task-tracker-backend";
        }
        if (authPath == null || authPath.isBlank()) {
            authPath = "/api/v1/auth/login";
        }
        if (usersPath == null || usersPath.isBlank()) {
            usersPath = "/api/v1/users";
        }
    }

    public String baseUrl() {
        return "http://" + serviceName;
    }
}
