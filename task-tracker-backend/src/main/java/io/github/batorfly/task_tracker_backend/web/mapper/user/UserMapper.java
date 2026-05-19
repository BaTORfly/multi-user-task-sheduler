package io.github.batorfly.task_tracker_backend.web.mapper.user;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.web.dto.user.UserWithoutTasksDto;
import io.github.batorfly.task_tracker_backend.web.mapper.Mappable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends Mappable<User, UserWithoutTasksDto> {

}
