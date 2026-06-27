package io.github.batorfly.task_tracker_backend.service.user;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.repository.user.UserRepository;
import io.github.batorfly.task_tracker_backend.dto.user.UserWithTasksDto;
import io.github.batorfly.task_tracker_backend.mapper.user.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserWithTasksDto> getAllUsersWithTasks() {
        return userRepository.getAllUsersWithTasks().stream().map(userMapper::toDtoWithTasks).toList();
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.empty();
    }
}
