package io.github.batorfly.task_tracker_backend.web.api.controller.auth;

import io.github.batorfly.task_tracker_backend.service.auth.AuthService;
import io.github.batorfly.task_tracker_backend.web.dto.auth.AuthResponseForm;
import io.github.batorfly.task_tracker_backend.web.dto.auth.LoginForm;
import io.github.batorfly.task_tracker_backend.web.dto.auth.SignupForm;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor @Slf4j
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseForm register(
            @Valid @RequestBody SignupForm signupForm,
            HttpServletResponse response
    ) {
        return authService.register(signupForm, response);
    }

    public AuthResponseForm login(
            @Valid @RequestBody LoginForm loginForm,
            HttpServletResponse response
    ){
        return authService.login(loginForm, response);
    }
}
