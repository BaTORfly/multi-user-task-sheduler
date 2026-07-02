package io.github.batorfly.task_tracker_backend.service.kafka;

import io.github.batorfly.task_tracker_backend.dto.email.EmailDto;
import io.github.batorfly.task_tracker_backend.exception.UnexpectedServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Random;

@Service
@RequiredArgsConstructor @Slf4j
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String EMAIL_SENDING_TOPIC = "EMAIL_SENDING_TASKS";
    private final String WELCOME_HEADER = "Welcome to our team, %s!";
    private final String WELCOME_SUBJECT = "Hi, %s!\\r\\nYou've created account with email '%s'. We hope you'll love our product!";


    public void sendWelcomeEmail(String email, String firstName){
        try{
            EmailDto emailToSend = new EmailDto(
                    email,
                    WELCOME_HEADER.formatted(firstName.trim()),
                    WELCOME_SUBJECT.formatted(firstName.trim(), email)
            );
            String json = objectMapper.writeValueAsString(emailToSend);
            kafkaTemplate.send(EMAIL_SENDING_TOPIC, "%d".formatted(new Random().nextInt(0,3)), json);
        } catch (JacksonException ex){
            log.error("Message can't be converted to json format ");
            throw new UnexpectedServerException(ex);
        }

    }

}
