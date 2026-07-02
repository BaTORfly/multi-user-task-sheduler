package io.github.batorfly.task_tracker_backend.integration;

import io.github.batorfly.task_tracker_backend.dto.auth.SignupForm;
import io.github.batorfly.task_tracker_backend.dto.email.EmailDto;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@EmbeddedKafka(
        partitions = 3,
        topics = KafkaIntegrationTest.EMAIL_SENDING_TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
class KafkaIntegrationTest {
    static final String EMAIL_SENDING_TOPIC = "EMAIL_SENDING_TASKS";
    private static final String SIGN_UP_URL = "/api/v1/auth/signup";

    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("kafka-integration-tests-db")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE users, roles, tasks RESTART IDENTITY CASCADE");
    }

    @Test
    void signupShouldSendWelcomeEmailToKafka() throws Exception {
        SignupForm request = new SignupForm(
                "John",
                "Smith",
                "john.smith@example.com",
                "StrongPass123"
        );

        EmbeddedKafkaBroker embeddedKafkaBroker = applicationContext.getBean(
                EmbeddedKafkaBroker.BEAN_NAME,
                EmbeddedKafkaBroker.class
        );
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                embeddedKafkaBroker,
                "kafka-integration-test",
                true
        );
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (Consumer<String, String> consumer =
                     new DefaultKafkaConsumerFactory<>(
                             consumerProps,
                             new StringDeserializer(),
                             new StringDeserializer()
                     ).createConsumer()) {
            embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, EMAIL_SENDING_TOPIC);

            mockMvc.perform(post(SIGN_UP_URL)
                            .content(mapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.access_token", notNullValue()),
                            cookie().exists("refreshToken")
                    );

            ConsumerRecord<String, String> record =
                    KafkaTestUtils.getSingleRecord(consumer, EMAIL_SENDING_TOPIC);
            EmailDto emailDto = mapper.readValue(record.value(), EmailDto.class);

            Assertions.assertTrue(Set.of("0", "1", "2").contains(record.key()));
            Assertions.assertEquals(request.email(), emailDto.to());
            Assertions.assertEquals("Welcome to our team, John!", emailDto.header());
            Assertions.assertEquals(
                    "Hi, John!\\r\\nYou've created account with email 'john.smith@example.com'. We hope you'll love our product!",
                    emailDto.text()
            );
        }
    }
}
