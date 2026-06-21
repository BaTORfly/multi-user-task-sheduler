package io.github.batorfly.task_tracker_backend.service.task;

import io.github.batorfly.task_tracker_backend.domain.task.Task;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.batorfly.task_tracker_backend.exception.auth.AuthorizationFailedException;
import io.github.batorfly.task_tracker_backend.exception.task.TaskNotFoundException;
import io.github.batorfly.task_tracker_backend.repository.task.TaskRepository;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.web.mapper.task.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public TaskDto deleteTaskById(Long taskId, User currentUser) {
        Task taskToDelete = taskRepository.findByIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new AuthorizationFailedException(
                        "User doesn't have rights to delete this task"
                ));

        taskRepository.delete(taskToDelete);

        return taskMapper.toDto(taskToDelete);
    }

    @Override
    @Transactional
    public TaskDto updateTask(TaskDto taskDto, Long taskId, User currentUser) {
        Task taskToUpdate = taskRepository.findByIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new AuthenticationFailedException(
                        "User doesn't have rights to change this task"
                ));

        taskToUpdate.setTitle(taskDto.title());
        taskToUpdate.setDescription(taskDto.description());
        taskToUpdate.setDone(taskDto.isDone());

        return taskMapper.toDto(taskRepository.save(taskToUpdate));
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
