package io.github.batorfly.task_tracker_backend.service.task;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.repository.task.TaskRepository;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;

    @Override
    public TaskDto saveTask(User user, TaskDto task) {
        return null;
    }

    @Override
    public TaskDto deleteTaskById(Long id) {
        return null;
    }

    @Override
    public boolean userHasTask(User user, Long taskId) {
        return false;
    }

    @Override
    public TaskDto updateTask(TaskDto taskForm, Long taskId) {
        return null;
    }

    @Override
    public List<TaskDto> getUserTasks(User user) {
        return List.of();
    }

    @Override
    public TaskDto getUserTaskById(User user, Long taskId) {
        return null;
    }
}
