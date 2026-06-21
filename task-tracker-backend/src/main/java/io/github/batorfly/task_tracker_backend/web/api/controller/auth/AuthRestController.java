package io.github.batorfly.task_tracker_backend.web.api.controller.auth;

import io.github.batorfly.task_tracker_backend.service.auth.AuthService;
import io.github.batorfly.task_tracker_backend.web.dto.auth.AuthResponseForm;
import io.github.batorfly.task_tracker_backend.web.dto.auth.LoginForm;
import io.github.batorfly.task_tracker_backend.web.dto.auth.SignupForm;
import io.github.batorfly.task_tracker_backend.web.dto.error.ErrorResponse;
import io.github.batorfly.task_tracker_backend.web.dto.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor @Slf4j
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns an access token. A refresh token is written to the response cookies."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully.",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponseForm.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Signup form validation failed.",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with the provided email already exists.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
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
