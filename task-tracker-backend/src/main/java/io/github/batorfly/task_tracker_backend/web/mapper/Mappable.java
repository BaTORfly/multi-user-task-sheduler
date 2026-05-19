package io.github.batorfly.task_tracker_backend.web.mapper;

import java.util.List;

/**
 * Базовый интерфейс для всех мапперов
 * @param <E> Entity тип (Task, User, etc.)
 * @param <D> DTO тип (TaskDto, UserDto, etc.)
 */
public interface Mappable<E, D>{
    D toDto(E entity);
    List<D> toDto(List<E> entities);
    E toEntity(D dto);
    List<E> toEntity(List<D> dtos);
}
