package io.github.batorfly.task_tracker_backend.service.task;

import io.github.batorfly.task_tracker_backend.domain.task.Task;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.repository.task.TaskRepository;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.web.mapper.task.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto saveTask(User user, TaskDto taskDto) {
        Task newTask = new Task();

        newTask.setTitle(taskDto.title());
        newTask.setDescription(taskDto.description());
        newTask.setDone(taskDto.isDone());
        newTask.setUser(user);

        return taskMapper.toDto(taskRepository.save(newTask));
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
