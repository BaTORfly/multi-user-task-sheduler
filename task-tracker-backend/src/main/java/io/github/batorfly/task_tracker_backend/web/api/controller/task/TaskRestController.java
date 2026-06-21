package io.github.batorfly.task_tracker_backend.web.api.controller.task;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.service.task.TaskService;
import io.github.batorfly.task_tracker_backend.web.dto.error.ErrorResponse;
import io.github.batorfly.task_tracker_backend.web.dto.error.ValidationErrorResponse;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

import java.util.List;

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

    @Operation(
            summary = "Update a task",
            description = "Updates an existing task that belongs to the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully.",
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
                    description = "Authentication failed or user has no rights to update this task.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PutMapping("/{taskId}")
    public TaskDto updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDto task,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.updateTask(task, taskId, currentUser);
    }

    @Operation(
            summary = "Delete a task",
            description = "Deletes an existing task that belongs to the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task deleted successfully.",
                    content = @Content(
                            schema = @Schema(implementation = TaskDto.class)
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
                    description = "Access denied or user has no rights to delete this task.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskDto deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.deleteTaskById(taskId, currentUser);
    }

    @Operation(
            summary = "Get a user task",
            description = "Returns an existing task that belongs to the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task found successfully.",
                    content = @Content(
                            schema = @Schema(implementation = TaskDto.class)
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
                    description = "Task not found or access denied.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public TaskDto getUserTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.getUserTaskById(currentUser, taskId);
    }

    @Operation(
            summary = "Get user tasks",
            description = "Returns all tasks that belong to the authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks found successfully.",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = TaskDto.class))
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
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TaskDto> getUserTasks(
            @AuthenticationPrincipal User currentUser
    ) {
        return taskService.getUserTasks(currentUser);
    }
}
