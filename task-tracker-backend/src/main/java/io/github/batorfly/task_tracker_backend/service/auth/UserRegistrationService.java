package io.github.batorfly.task_tracker_backend.service.auth;

import io.github.batorfly.task_tracker_backend.domain.user.Authority;
import io.github.batorfly.task_tracker_backend.domain.user.Roles;
import io.github.batorfly.task_tracker_backend.domain.user.User;
import io.github.batorfly.task_tracker_backend.exception.user.UserAlreadyExists;
import io.github.batorfly.task_tracker_backend.service.authority.AuthorityService;
import io.github.batorfly.task_tracker_backend.service.user.UserService;
import io.github.batorfly.task_tracker_backend.dto.auth.SignupForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class UserRegistrationService {
    private final UserService userService;
    private final AuthorityService authorityService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(SignupForm signupForm) {
        User user = buildUserFromSignupForm(signupForm);
        Authority authority = createUserAuthority(user);

        try{
            userService.saveUser(user);
            authorityService.save(authority);
        } catch (DataIntegrityViolationException ex){
            log.error("User passed login already exists", ex);
            throw new UserAlreadyExists("User with email %s already exists".formatted(user.getEmail()), ex);
        }

        return user;
    }

    private User buildUserFromSignupForm(SignupForm form) {
        return User.builder()
                .firstName(form.firstName())
                .lastName(form.lastName())
                .email(form.email())
                .password(passwordEncoder.encode(form.password()))
                .enabled(true)
                .build();
    }

    private Authority createUserAuthority(User user) {
        return Authority.builder()
                .role(Roles.USER)
                .user(user)
                .build();
    }
}
