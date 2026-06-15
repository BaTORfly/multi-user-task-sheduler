package io.github.batorfly.task_tracker_backend.service.auth;

import io.github.batorfly.task_tracker_backend.web.dto.auth.AuthResponseForm;
import io.github.batorfly.task_tracker_backend.web.dto.auth.LoginForm;
import io.github.batorfly.task_tracker_backend.web.dto.auth.SignupForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponseForm register(SignupForm signupForm, HttpServletResponse response);
    AuthResponseForm login(LoginForm loginForm, HttpServletResponse response);
    void logout(HttpServletRequest request, HttpServletResponse response);
    AuthResponseForm refreshToken(HttpServletRequest request);
}
