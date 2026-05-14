package io.github.batorfly.task_tracker_backend.domain.task;

import io.github.batorfly.task_tracker_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {
    private Long id;

    private String title;

    private String description;

    private User user;

    private boolean isDone;

    private Timestamp created;

    private Timestamp updated;

    private Timestamp completionTime;
}
