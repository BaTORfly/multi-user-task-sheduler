package io.github.batorfly.task_tracker_backend.web.api.controller.task;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.service.task.TaskService;
import io.github.batorfly.task_tracker_backend.web.dto.error.ErrorResponse;
import io.github.batorfly.task_tracker_backend.web.dto.error.ValidationErrorResponse;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor @Slf4j
public class TaskRestController {
    private final TaskService taskService;

    @Operation(
            summary = "Create a task",
            description = "Creates a new task for the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully.",
                    content = @Content(
                            schema = @Schema(implementation = TaskDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Task form validation failed.",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class)
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
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public TaskDto createTask(
            @Valid @RequestBody TaskDto taskDto,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.saveTask(currentUser, taskDto);
    }
}
