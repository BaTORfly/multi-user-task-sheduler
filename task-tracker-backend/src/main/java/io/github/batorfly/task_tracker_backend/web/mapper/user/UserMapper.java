package io.github.batorfly.task_tracker_backend.web.mapper.user;

import io.github.batorfly.task_tracker_backend.domain.task.Task;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.web.dto.user.UserWithTasksDto;
import io.github.batorfly.task_tracker_backend.web.dto.user.UserWithoutTasksDto;
import io.github.batorfly.task_tracker_backend.web.mapper.Mappable;
import io.github.batorfly.task_tracker_backend.web.mapper.task.TaskMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper implements Mappable<User, UserWithoutTasksDto> {

    @Autowired
    protected TaskMapper taskMapper;

    // User -> UserWithoutTasksDto
    @Override
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    public abstract UserWithoutTasksDto toDto(User entity);

    // User -> UserWithTasksDto
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "tasks", expression = "java(mapTasksToDto(user.getTasks()))")
    public abstract UserWithTasksDto toDtoWithTasks(User user);

    // UserWithoutTasksDto -> User
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    public abstract User toEntity(UserWithoutTasksDto dto);

    // Вспомогательный метод для маппинга Set<Task> -> Set<TaskDto>
    protected Set<TaskDto> mapTasksToDto(Set<Task> tasks) {
        if (tasks == null) return null;
        return tasks.stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toSet());
    }
}
