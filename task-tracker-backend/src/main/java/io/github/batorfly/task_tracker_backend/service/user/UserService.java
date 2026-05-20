package io.github.batorfly.task_tracker_backend.service.user;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.web.dto.user.UserWithTasksDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);

    Optional<User> findByEmail(String email);

    List<UserWithTasksDto> getAllUsersWithTasks();

    Optional<User> findById(Long userId);
}
