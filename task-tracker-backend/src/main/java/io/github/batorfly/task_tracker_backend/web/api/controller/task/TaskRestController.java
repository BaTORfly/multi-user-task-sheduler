package io.github.batorfly.task_tracker_backend.web.api.controller.task;

import io.github.batorfly.task_tracker_backend.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskRestController {
    private final TaskService taskService;
}
