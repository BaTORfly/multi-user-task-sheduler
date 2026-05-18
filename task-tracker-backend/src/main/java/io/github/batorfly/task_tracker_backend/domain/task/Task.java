package io.github.batorfly.task_tracker_backend.domain.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "tasks")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Setter(AccessLevel.NONE)
    @Column(name = "done", nullable = false)
    private boolean isDone;

    @Column(name = "created_time", nullable = false, updatable = false)
    private Instant createdTime;

    @Column(name = "updatedTime", nullable = false)
    private Instant updatedTime;

    @Column(name = "completion_time")
    private Instant completionTime;

    @PrePersist
    protected void onCreate(){
        Instant now = Instant.now();
        createdTime = now;
        updatedTime = now;
    }

    @PreUpdate
    protected void onUpdate(){
        updatedTime = Instant.now();

        if (isDone && completionTime == null){
            completionTime = Instant.now();
        }
    }

    public boolean isDone(){
        return isDone;
    }

    public void setDone(boolean done){
        this.isDone = done;

        if (this.isDone){
            this.completionTime = Instant.now();
        } else{
            this.completionTime = null;
        }
    }
}
