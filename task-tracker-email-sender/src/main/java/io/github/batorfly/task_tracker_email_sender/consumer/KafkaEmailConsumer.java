package io.github.batorfly.task_tracker_email_sender.consumer;

import io.github.batorfly.task_tracker_email_sender.dto.EmailDto;
import io.github.batorfly.task_tracker_email_sender.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor @Slf4j
public class KafkaEmailConsumer {
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "EMAIL_SENDING_TASKS", concurrency = "3")
    public void sendEmail(String eDto) {
        try {
            EmailDto emailDto = objectMapper.readValue(eDto, EmailDto.class);
            mailService.sendEmail(emailDto.to(), emailDto.header(), emailDto.text());
            log.info("Text {} with header {} sent to {}", emailDto.text(), emailDto.header(), emailDto.to());
        } catch (JacksonException e) {
            throw new  RuntimeException(e);
        } catch (MailException e) {
            log.error("Can't send email", e);
        }
    }
}
