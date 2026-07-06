package io.github.batorfly.task_tracker_scheduler.integration;

import io.github.batorfly.task_tracker_scheduler.dto.email.EmailCommand;
import io.github.batorfly.task_tracker_scheduler.service.kafka.EmailCommandPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "EMAIL_SENDING_TASKS")
class KafkaPublishingIntegrationTest {

    @Autowired
    private EmailCommandPublisher publisher;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void publishesEmailCommandAsJson() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("scheduler-test", "true", embeddedKafkaBroker);
        consumerProps.put("key.deserializer", StringDeserializer.class);
        consumerProps.put("value.deserializer", StringDeserializer.class);

        try (Consumer<String, String> consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps)
                .createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "EMAIL_SENDING_TASKS");

            publisher.publish(new EmailCommand(
                    "user@example.com",
                    "You've got 1 unfinished task!",
                    "Good night, John Smith!\nYou've got 1 unfinished task:\n1. Prepare report"
            ));

            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "EMAIL_SENDING_TASKS");

            assertThat(record.key()).isEqualTo("user@example.com");
            assertThat(record.value()).contains("\"to\":\"user@example.com\"");
            assertThat(record.value()).contains("\"header\":\"You've got 1 unfinished task!\"");
        }
    }
}
