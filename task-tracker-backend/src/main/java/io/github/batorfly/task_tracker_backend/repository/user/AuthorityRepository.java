package io.github.batorfly.task_tracker_backend.repository.user;

import io.github.batorfly.task_tracker_backend.domain.user.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
