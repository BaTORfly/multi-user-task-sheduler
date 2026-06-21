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
            description = "Creates a new user account and returns an access token. A refresh token is written to the response cookies.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Signup form with validated user profile and credentials.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SignupForm.class),
                            examples = @ExampleObject(
                                    name = "Valid signup request",
                                    value = """
                                            {
                                              "first_name": "John",
                                              "last_name": "Smith",
                                              "email": "john.smith@example.com",
                                              "password": "StrongPass123"
                                            }
                                            """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AuthResponseForm.class),
                            examples = @ExampleObject(
                                    name = "Successful signup response",
                                    value = """
                                            {
                                              "access_token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLnNtaXRoQGV4YW1wbGUuY29tIn0.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Signup form validation failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Validation error response",
                                    value = """
                                            {
                                              "errorFields": {
                                                "firstName": "First name must be between 3 and 64 characters",
                                                "lastName": "Last name must include only letters",
                                                "email": "Invalid email",
                                                "password": "Password must contain at least one uppercase and one lowercase letter"
                                              },
                                              "timestamp": 1782045600000
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with the provided email already exists.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "User already exists response",
                                    value = """
                                            {
                                              "message": "User already exists",
                                              "timestamp": 1782045600000
                                            }
                                            """
                            )
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
