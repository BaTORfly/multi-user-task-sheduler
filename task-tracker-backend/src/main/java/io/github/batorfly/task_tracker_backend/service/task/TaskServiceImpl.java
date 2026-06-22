package io.github.batorfly.task_tracker_backend.service.task;

import io.github.batorfly.task_tracker_backend.domain.task.Task;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.exception.auth.AuthenticationFailedException;
import io.github.batorfly.task_tracker_backend.exception.auth.AuthorizationFailedException;
import io.github.batorfly.task_tracker_backend.repository.task.TaskRepository;
import io.github.batorfly.task_tracker_backend.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.mapper.task.TaskMapper;
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
    @Transactional(readOnly = true)
    public List<TaskDto> getUserTasks(User currentUser) {
        return taskRepository.findAllByUserId(currentUser.getId())
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getUserTaskById(User currentUser, Long taskId) {

        Task task = taskRepository.findByIdAndUserId(taskId, currentUser.getId())
                .orElseThrow(() -> new AuthorizationFailedException(
                        "Task not found or access denied"
                ));

        return taskMapper.toDto(task);
    }
}
