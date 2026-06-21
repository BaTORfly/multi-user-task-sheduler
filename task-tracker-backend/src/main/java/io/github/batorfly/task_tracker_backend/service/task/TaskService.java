package io.github.batorfly.task_tracker_backend.service.task;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;

import java.util.List;

public interface TaskService {
    TaskDto saveTask(User user, TaskDto task);

    TaskDto deleteTaskById(Long taskId, User currentUser);

    TaskDto updateTask(TaskDto taskDto, Long taskId, User currentUser);

    List<TaskDto> getUserTasks(User user);

    TaskDto getUserTaskById(User user, Long taskId);
}
