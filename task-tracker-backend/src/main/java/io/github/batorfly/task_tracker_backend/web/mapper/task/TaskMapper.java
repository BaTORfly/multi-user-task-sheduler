package io.github.batorfly.task_tracker_backend.web.mapper.task;

import io.github.batorfly.task_tracker_backend.domain.task.Task;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.web.mapper.Mappable;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper extends Mappable<Task, TaskDto> {

    @Override
    default TaskDto toDto(Task entity) {
        return null;
    }

    @Override
    default List<TaskDto> toDto(List<Task> entities) {
        return List.of();
    }

    @Override
    default Task toEntity(TaskDto dto) {
        return null;
    }

    @Override
    default List<Task> toEntity(List<TaskDto> dtos) {
        return List.of();
    }
}
