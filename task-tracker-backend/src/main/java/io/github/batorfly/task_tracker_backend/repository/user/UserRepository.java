package io.github.batorfly.task_tracker_backend.repository.user;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.tasks WHERE u.enabled = true")
    List<User> getAllUsersWithTasks();

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findByEmail(String email);
}