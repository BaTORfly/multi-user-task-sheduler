package io.github.batorfly.task_tracker_backend.web.api.controller.user;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.service.user.UserService;
import io.github.batorfly.task_tracker_backend.web.dto.error.ErrorResponse;
import io.github.batorfly.task_tracker_backend.web.dto.user.UserWithTasksDto;
import io.github.batorfly.task_tracker_backend.web.dto.user.UserWithoutTasksDto;
import io.github.batorfly.task_tracker_backend.web.mapper.user.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserMapper userMapper;
    private final UserService userService;

    @Operation(
            summary = "Get current user data",
            description = "Returns data of the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Current user data returned successfully.",
                    content = @Content(
                            schema = @Schema(implementation = UserWithoutTasksDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserWithoutTasksDto getCurrentUserData(@AuthenticationPrincipal User currentUser) {
        return userMapper.toDto(currentUser);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserWithTasksDto> getAllUsersWithTasks() {
        return userService.getAllUsersWithTasks();
    }
}
