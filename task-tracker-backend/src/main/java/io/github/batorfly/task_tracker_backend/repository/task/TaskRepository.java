package io.github.batorfly.task_tracker_backend.repository.task;


import io.github.batorfly.task_tracker_backend.domain.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
