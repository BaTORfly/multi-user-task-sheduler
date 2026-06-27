package io.github.batorfly.task_tracker_backend.controller;

import io.github.batorfly.task_tracker_backend.api.controller.user.UserRestController;
import io.github.batorfly.task_tracker_backend.config.SecurityConfig;
import io.github.batorfly.task_tracker_backend.domain.user.Authority;
import io.github.batorfly.task_tracker_backend.domain.user.Roles;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.dto.user.UserWithTasksDto;
import io.github.batorfly.task_tracker_backend.dto.user.UserWithoutTasksDto;
import io.github.batorfly.task_tracker_backend.mapper.user.UserMapper;
import io.github.batorfly.task_tracker_backend.service.jwt.JwtService;
import io.github.batorfly.task_tracker_backend.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Import(SecurityConfig.class)
@WebMvcTest(UserRestController.class)
public class UserRestControllerTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_FIRST_NAME = "First Name";
    private static final String TEST_LAST_NAME = "Last Name";
    private static final String USERS_URL = "/api/v1/users";
    private static final String CURRENT_USER_URL = USERS_URL + "/current";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;

    private final User mockCurrentUser = User.builder()
            .id(TEST_ID)
            .firstName(TEST_FIRST_NAME)
            .lastName(TEST_LAST_NAME)
            .email(TEST_EMAIL)
            .authorities(Set.of(new Authority(Roles.USER)))
            .enabled(true)
            .build();

    private User mockAdmin;
    private UserWithoutTasksDto currentUserDto;
    private List<UserWithTasksDto> allUsers;

    @BeforeEach
    void setUp() {
        mockAdmin = User.builder()
                .id(TEST_ID)
                .firstName("Admin")
                .lastName("User")
                .email("admin@email.com")
                .authorities(Set.of(new Authority(Roles.ADMIN)))
                .enabled(true)
                .build();

        currentUserDto = new UserWithoutTasksDto(
                mockCurrentUser.getId(),
                mockCurrentUser.getEmail(),
                mockCurrentUser.getFirstName(),
                mockCurrentUser.getLastName()
        );

        allUsers = List.of(
                // In this controller test UserService already returns DTOs, so a real UserMapper is not needed here.
                new UserWithTasksDto(
                        2L,
                        "user1@email.com",
                        "FirstName1",
                        "LastName1",
                        Set.of(
                                new TaskDto(1L, "Title1", "Desc1", false, null),
                                new TaskDto(2L, "Title2", "Desc2", false, null)
                        )
                ),
                new UserWithTasksDto(
                        3L,
                        "user2@email.com",
                        "FirstName2",
                        "LastName2",
                        Set.of()
                )
        );
    }

    @Nested
    class GetCurrentUserDataTests {
        @Test
        void getCurrentUserData_shouldResponseWithCurrentUserAndOkStatusCode() throws Exception {
            when(userMapper.toDto(mockCurrentUser))
                    .thenReturn(currentUserDto);

            mockMvc.perform(get(CURRENT_USER_URL)
                            .with(authenticatedUser(mockCurrentUser)))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(currentUserDto.id()),
                            jsonPath("$.email").value(currentUserDto.email()),
                            jsonPath("$.first_name").value(currentUserDto.firstName()),
                            jsonPath("$.last_name").value(currentUserDto.lastName())
                    );

            verify(userMapper).toDto(mockCurrentUser);
        }
    }

    @Nested
    class GetAllUsersWithTasksTests {
        @Test
        void getAllUsersWithTasks_shouldResponseWithUsersAndOkStatusCodeWhenUserIsAdmin() throws Exception {
            when(userService.getAllUsersWithTasks())
                    .thenReturn(allUsers);

            mockMvc.perform(get(USERS_URL)
                            .with(authenticatedUser(mockAdmin)))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.length()").value(2),
                            jsonPath("$[0].id").value(allUsers.get(0).id()),
                            jsonPath("$[0].email").value(allUsers.get(0).email()),
                            jsonPath("$[0].first_name").value(allUsers.get(0).firstName()),
                            jsonPath("$[0].last_name").value(allUsers.get(0).lastName()),
                            jsonPath("$[0].tasks.length()").value(2),
                            jsonPath("$[1].id").value(allUsers.get(1).id()),
                            jsonPath("$[1].email").value(allUsers.get(1).email()),
                            jsonPath("$[1].first_name").value(allUsers.get(1).firstName()),
                            jsonPath("$[1].last_name").value(allUsers.get(1).lastName()),
                            jsonPath("$[1].tasks.length()").value(0)
                    );

            verify(userService).getAllUsersWithTasks();
        }

        @Test
        void getAllUsersWithTasks_shouldReturnForbiddenStatusCodeWhenUserIsNotAdmin() throws Exception {
            mockMvc.perform(get(USERS_URL)
                            .with(authenticatedUser(mockCurrentUser)))
                    .andExpectAll(
                            status().isForbidden(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.timestamp").exists()
                    );
        }
    }

    private RequestPostProcessor authenticatedUser(User user) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        return authentication(authentication);
    }
}
