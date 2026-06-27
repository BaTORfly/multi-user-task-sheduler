package io.github.batorfly.task_tracker_backend.controller;

import io.github.batorfly.task_tracker_backend.api.controller.task.TaskRestController;
import io.github.batorfly.task_tracker_backend.config.SecurityConfig;
import io.github.batorfly.task_tracker_backend.domain.user.Authority;
import io.github.batorfly.task_tracker_backend.domain.user.Roles;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.exception.auth.AuthorizationFailedException;
import io.github.batorfly.task_tracker_backend.exception.task.TaskNotFoundException;
import io.github.batorfly.task_tracker_backend.service.jwt.JwtService;
import io.github.batorfly.task_tracker_backend.service.task.TaskService;
import io.github.batorfly.task_tracker_backend.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(controllers = TaskRestController.class)
public class TaskRestControllerTest {
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "TestPassword1.";
    private static final String TEST_FIRST_NAME = "FirstName";
    private static final String TEST_LAST_NAME = "LastName";
    private static final String TASKS_URL = "/api/v1/tasks";
    private static final Long TASK_ID = 1L;
    private static final String TASK_NOT_FOUND_MESSAGE = "Task not found";
    private static final String ACCESS_DENIED_MESSAGE = "Task not found or access denied";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private TaskService taskService;
    @MockitoBean
    private UserService userService;

    private final User mockUser = User.builder()
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .password(TEST_PASSWORD)
            .authorities(Set.of(new Authority(Roles.USER)))
            .enabled(true).build();

    private final TaskDto invalidTask = new TaskDto(4L, "", "Desc4", false, null);
    private final TaskDto firstTask = new TaskDto(
            TASK_ID,
            "Task 1",
            "Desc1",
            false,
            null
    );
    private final TaskDto secondTask = new TaskDto(
            2L,
            "Task 2",
            "Desc2",
            true,
            Instant.parse("2026-06-28T00:00:00Z")
    );
    private final TaskDto createTaskRequest = new TaskDto(
            null,
            "New task",
            "New desc",
            false,
            null
    );
    private final TaskDto createdTask = new TaskDto(
            3L,
            createTaskRequest.title(),
            createTaskRequest.description(),
            createTaskRequest.isDone(),
            null
    );
    private final TaskDto updateTaskRequest = new TaskDto(
            null,
            "Updated task",
            "Updated desc",
            true,
            null
    );
    private final TaskDto updatedTask = new TaskDto(
            TASK_ID,
            updateTaskRequest.title(),
            updateTaskRequest.description(),
            updateTaskRequest.isDone(),
            Instant.parse("2026-06-28T00:30:00Z")
    );

    @BeforeEach
    void setUp() {
        setUpSecurityContext();
    }

    @Nested
    class GetUserTasksTests {
        @Test
        void getUserTasks_shouldResponseWithUserTasksAndOkStatusCode() throws Exception {
            when(taskService.getUserTasks(mockUser))
                    .thenReturn(List.of(firstTask, secondTask));

            mockMvc.perform(get(TASKS_URL)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.length()").value(2),
                            jsonPath("$[0].id").value(firstTask.id()),
                            jsonPath("$[0].title").value(firstTask.title()),
                            jsonPath("$[0].description").value(firstTask.description()),
                            jsonPath("$[0].done").value(firstTask.isDone()),
                            jsonPath("$[1].id").value(secondTask.id()),
                            jsonPath("$[1].title").value(secondTask.title()),
                            jsonPath("$[1].description").value(secondTask.description()),
                            jsonPath("$[1].done").value(secondTask.isDone()),
                            jsonPath("$[1].completion_time").value(secondTask.completionTime().toString())
                    );

            verify(taskService).getUserTasks(mockUser);
        }

        @Test
        void getUserTasks_shouldResponseWithNotFoundStatusCodeWhenTaskNotFoundExceptionThrown() throws Exception {
            when(taskService.getUserTasks(mockUser))
                    .thenThrow(new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

            mockMvc.perform(get(TASKS_URL)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.message").value(TASK_NOT_FOUND_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).getUserTasks(mockUser);
        }
    }

    @Nested
    class GetUserTaskTests {
        @Test
        void getUserTask_shouldResponseWithUserTaskAndOkStatusCode() throws Exception {
            when(taskService.getUserTaskById(mockUser, TASK_ID))
                    .thenReturn(firstTask);

            mockMvc.perform(get(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(firstTask.id()),
                            jsonPath("$.title").value(firstTask.title()),
                            jsonPath("$.description").value(firstTask.description()),
                            jsonPath("$.done").value(firstTask.isDone())
                    );

            verify(taskService).getUserTaskById(mockUser, TASK_ID);
        }

        @Test
        void getUserTask_shouldResponseWithNotFoundStatusCodeWhenTaskNotFoundExceptionThrown() throws Exception {
            when(taskService.getUserTaskById(mockUser, TASK_ID))
                    .thenThrow(new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

            mockMvc.perform(get(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.message").value(TASK_NOT_FOUND_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).getUserTaskById(mockUser, TASK_ID);
        }

        @Test
        void getUserTask_shouldResponseWithForbiddenStatusCodeWhenAuthorizationFailed() throws Exception {
            when(taskService.getUserTaskById(mockUser, TASK_ID))
                    .thenThrow(new AuthorizationFailedException(ACCESS_DENIED_MESSAGE));

            mockMvc.perform(get(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.message").value(ACCESS_DENIED_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).getUserTaskById(mockUser, TASK_ID);
        }
    }

    @Nested
    class CreateTasksTests {
        @Test
        void createTask_shouldResponseWithCreatedTaskAndCreatedStatusCode() throws Exception {
            when(taskService.saveTask(eq(mockUser), eq(createTaskRequest)))
                    .thenReturn(createdTask);

            mockMvc.perform(post(TASKS_URL)
                            .with(authenticatedUser())
                            .content(mapper.writeValueAsString(createTaskRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isCreated(),
                            jsonPath("$.id").value(createdTask.id()),
                            jsonPath("$.title").value(createdTask.title()),
                            jsonPath("$.description").value(createdTask.description()),
                            jsonPath("$.done").value(createdTask.isDone())
                    );

            verify(taskService).saveTask(mockUser, createTaskRequest);
        }

        @Test
        void createTask_shouldReturnBadRequestStatusCodeWhenTitleIsEmpty() throws Exception {
            mockMvc.perform(post(TASKS_URL)
                            .with(authenticatedUser())
                            .content(mapper.writeValueAsString(invalidTask))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.errorFields.title").value("Title cannot be empty")
                    );

            verify(taskService, never()).saveTask(any(User.class), any(TaskDto.class));
        }

        @Test
        void createTask_shouldResponseWithNotFoundStatusCodeWhenTaskNotFoundExceptionThrown() throws Exception {
            when(taskService.saveTask(eq(mockUser), eq(createTaskRequest)))
                    .thenThrow(new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

            mockMvc.perform(post(TASKS_URL)
                            .with(authenticatedUser())
                            .content(mapper.writeValueAsString(createTaskRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.message").value(TASK_NOT_FOUND_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).saveTask(mockUser, createTaskRequest);
        }
    }

    @Nested
    class UpdateTasksTests {
        @Test
        void updateTask_shouldResponseWithUpdatedTaskAndOkStatusCode() throws Exception {
            when(taskService.updateTask(updateTaskRequest, TASK_ID, mockUser))
                    .thenReturn(updatedTask);

            mockMvc.perform(put(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser())
                            .content(mapper.writeValueAsString(updateTaskRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(updatedTask.id()),
                            jsonPath("$.title").value(updatedTask.title()),
                            jsonPath("$.description").value(updatedTask.description()),
                            jsonPath("$.done").value(updatedTask.isDone()),
                            jsonPath("$.completion_time").value(updatedTask.completionTime().toString())
                    );

            verify(taskService).updateTask(updateTaskRequest, TASK_ID, mockUser);
        }

        @Test
        void updateTask_shouldReturnBadRequestStatusCodeWhenTitleIsEmpty() throws Exception {
            mockMvc.perform(put(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser())
                            .content(mapper.writeValueAsString(invalidTask))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.errorFields.title").value("Title cannot be empty")
                    );

            verify(taskService, never()).updateTask(any(TaskDto.class), eq(TASK_ID), any(User.class));
        }

        @Test
        void updateTask_shouldResponseWithNotFoundStatusCodeWhenTaskNotFoundExceptionThrown() throws Exception {
            when(taskService.updateTask(updateTaskRequest, TASK_ID, mockUser))
                    .thenThrow(new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

            mockMvc.perform(put(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser())
                            .content(mapper.writeValueAsString(updateTaskRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.message").value(TASK_NOT_FOUND_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).updateTask(updateTaskRequest, TASK_ID, mockUser);
        }
    }

    @Nested
    class DeleteTasksTests {
        @Test
        void deleteTask_shouldResponseWithDeletedTaskAndOkStatusCode() throws Exception {
            when(taskService.deleteTaskById(TASK_ID, mockUser))
                    .thenReturn(firstTask);

            mockMvc.perform(delete(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(firstTask.id()),
                            jsonPath("$.title").value(firstTask.title()),
                            jsonPath("$.description").value(firstTask.description()),
                            jsonPath("$.done").value(firstTask.isDone())
                    );

            verify(taskService).deleteTaskById(TASK_ID, mockUser);
        }

        @Test
        void deleteTask_shouldResponseWithNotFoundStatusCodeWhenTaskNotFoundExceptionThrown() throws Exception {
            when(taskService.deleteTaskById(TASK_ID, mockUser))
                    .thenThrow(new TaskNotFoundException(TASK_NOT_FOUND_MESSAGE));

            mockMvc.perform(delete(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isNotFound(),
                            jsonPath("$.message").value(TASK_NOT_FOUND_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).deleteTaskById(TASK_ID, mockUser);
        }

        @Test
        void deleteTask_shouldResponseWithForbiddenStatusCodeWhenAuthorizationFailed() throws Exception {
            when(taskService.deleteTaskById(TASK_ID, mockUser))
                    .thenThrow(new AuthorizationFailedException(ACCESS_DENIED_MESSAGE));

            mockMvc.perform(delete(TASKS_URL + "/{taskId}", TASK_ID)
                            .with(authenticatedUser()))
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.message").value(ACCESS_DENIED_MESSAGE),
                            jsonPath("$.timestamp").exists()
                    );

            verify(taskService).deleteTaskById(TASK_ID, mockUser);
        }
    }

    private void setUpSecurityContext() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private RequestPostProcessor authenticatedUser() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, mockUser.getAuthorities());
        return authentication(authentication);
    }
}
