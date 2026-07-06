package io.github.batorfly.task_tracker_scheduler.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scheduler")
public record SchedulerProperties(
        Credentials credentials,
        Reports reports,
        Kafka kafka
) {
    public record Credentials(String email, String password) {
    }

    public record Reports(String dailyCron, String zone) {
        public Reports {
            if (dailyCron == null || dailyCron.isBlank()) {
                dailyCron = "0 0 0 * * *";
            }
            if (zone == null || zone.isBlank()) {
                zone = "Europe/Moscow";
            }
        }
    }

    public record Kafka(String topic) {
        public Kafka {
            if (topic == null || topic.isBlank()) {
                topic = "EMAIL_SENDING_TASKS";
            }
        }
    }
}
