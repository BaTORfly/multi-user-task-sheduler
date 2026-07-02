package io.github.batorfly.task_tracker_backend.integration;

import io.github.batorfly.task_tracker_backend.domain.user.Authority;
import io.github.batorfly.task_tracker_backend.domain.user.Roles;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.dto.task.TaskDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class TaskIntegrationTest {
    private static final String TASKS_URL = "/api/v1/tasks";
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_USER_EMAIL = "test@gmail.com";

    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("integration-tests-db")
                    .withUsername("test")
                    .withPassword("test");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final User currentUser = User.builder()
            .id(TEST_USER_ID)
            .email(TEST_USER_EMAIL)
            .authorities(Set.of(new Authority(Roles.USER)))
            .enabled(true)
            .build();

    private final TaskDto taskDto = new TaskDto(
            null,
            "title3",
            "desc3",
            false,
            null
    );

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void setUp() {
        setUpSecurityContext();

        jdbcTemplate.execute("TRUNCATE TABLE users, roles, tasks RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("""
                INSERT INTO users (first_name, last_name, email, password, enabled)
                OVERRIDING SYSTEM VALUE
                VALUES ('first', 'last', 'test@gmail.com', 'test pass', true)
                """);
        jdbcTemplate.execute("""
                INSERT INTO roles (user_id, role)
                VALUES (1, 'USER')
                """);
        jdbcTemplate.execute("""
                INSERT INTO tasks (title, description, user_id, done, completion_time)
                VALUES
                    ('title1', 'desc1', 1, false, null),
                    ('title2', 'desc2', 1, true, CURRENT_TIMESTAMP)
                """);
    }

    @Test
    void shouldGetCurrentUserTasks() throws Exception {
        mockMvc.perform(get(TASKS_URL)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.getContext())))
                .andExpectAll(
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.length()").value(2),
                        jsonPath("$[*].id", containsInRelativeOrder(1, 2)),
                        jsonPath("$[*].title", containsInRelativeOrder("title1", "title2")),
                        jsonPath("$[*].description", containsInRelativeOrder("desc1", "desc2")),
                        jsonPath("$[*].done", containsInRelativeOrder(false, true)),
                        jsonPath("$[*].completion_time", containsInRelativeOrder(nullValue(), notNullValue()))
                );
    }

    @Test
    void shouldCreateNewTask() throws Exception {
        mockMvc.perform(post(TASKS_URL)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.getContext()))
                        .content(mapper.writeValueAsString(taskDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        status().isCreated(),
                        jsonPath("$.length()").value(5),
                        jsonPath("$.id").value(3),
                        jsonPath("$.title").value(taskDto.title()),
                        jsonPath("$.description").value(taskDto.description()),
                        jsonPath("$.done").value(false),
                        jsonPath("$.completion_time").value(nullValue())
                );

        Integer actual = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tasks WHERE task_id = 3 AND user_id = 1",
                Integer.class
        );

        Assertions.assertEquals(1, actual);
    }

    @Test
    void shouldUpdateTask() throws Exception {
        TaskDto toUpdate = new TaskDto(
                1L,
                "newTitle",
                "newDesc",
                true,
                null
        );

        mockMvc.perform(put(TASKS_URL + "/{taskId}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.getContext()))
                        .content(mapper.writeValueAsString(toUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        status().isOk(),
                        jsonPath("$.length()").value(5),
                        jsonPath("$.id").value(1),
                        jsonPath("$.title").value(toUpdate.title()),
                        jsonPath("$.description").value(toUpdate.description()),
                        jsonPath("$.done").value(true),
                        jsonPath("$.completion_time").value(notNullValue())
                );

        Boolean actual = jdbcTemplate.queryForObject(
                "SELECT done FROM tasks WHERE task_id = 1 AND user_id = 1",
                Boolean.class
        );

        Assertions.assertEquals(Boolean.TRUE, actual);
    }

    @Test
    void shouldDeleteTask() throws Exception {
        mockMvc.perform(delete(TASKS_URL + "/{taskId}", 2L)
                        .with(SecurityMockMvcRequestPostProcessors.securityContext(SecurityContextHolder.getContext())))
                .andExpect(status().isOk());

        Integer actual = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tasks WHERE task_id = 2",
                Integer.class
        );

        Assertions.assertEquals(0, actual);
    }

    private void setUpSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities());
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

}
