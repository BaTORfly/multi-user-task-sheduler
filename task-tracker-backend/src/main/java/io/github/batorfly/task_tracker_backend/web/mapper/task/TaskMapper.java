package io.github.batorfly.task_tracker_backend.web.mapper.task;

import io.github.batorfly.task_tracker_backend.domain.task.Task;
import io.github.batorfly.task_tracker_backend.web.dto.task.TaskDto;
import io.github.batorfly.task_tracker_backend.web.mapper.Mappable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper extends Mappable<Task, TaskDto> {

    @Override
    @Mapping(target = "isDone", source = "done")
    @Mapping(target = "completionTime", source = "completionTime")
    TaskDto toDto(Task entity);

    @Override
    @Mapping(target = "done", source = "isDone")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "completionTime", ignore = true)
    Task toEntity(TaskDto dto);
}
