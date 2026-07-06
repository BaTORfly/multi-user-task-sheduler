package io.github.batorfly.task_tracker_scheduler.service.kafka;

import io.github.batorfly.task_tracker_scheduler.config.SchedulerProperties;
import io.github.batorfly.task_tracker_scheduler.dto.email.EmailCommand;
import io.github.batorfly.task_tracker_scheduler.exception.EmailPublishingException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class EmailCommandPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final SchedulerProperties schedulerProperties;

    public void publish(EmailCommand command) {
        try {
            String payload = objectMapper.writeValueAsString(command);
            kafkaTemplate.send(schedulerProperties.kafka().topic(), command.to(), payload).get();
        } catch (JacksonException e) {
            throw new EmailPublishingException("Could not serialize email command", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EmailPublishingException("Kafka publishing was interrupted", e);
        } catch (ExecutionException e) {
            throw new EmailPublishingException("Could not publish email command to Kafka", e);
        }
    }
}
