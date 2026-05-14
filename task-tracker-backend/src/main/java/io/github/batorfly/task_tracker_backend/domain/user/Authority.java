package io.github.batorfly.task_tracker_backend.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Builder @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = {"id", "role"})
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    private User user;

    private Roles role;

    @Override
    public @Nullable String getAuthority() {
        return this.role.name();
    }

    public Authority(Roles role) {
        this.role = role;
    }

    public enum Roles {
        USER, ADMIN
    }
}
