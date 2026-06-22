package io.github.batorfly.task_tracker_backend.api.controller.auth;

import io.github.batorfly.task_tracker_backend.service.auth.AuthService;
import io.github.batorfly.task_tracker_backend.dto.auth.AuthResponseForm;
import io.github.batorfly.task_tracker_backend.dto.auth.LoginForm;
import io.github.batorfly.task_tracker_backend.dto.auth.SignupForm;
import io.github.batorfly.task_tracker_backend.dto.error.ErrorResponse;
import io.github.batorfly.task_tracker_backend.dto.error.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseForm register(
            @Valid @RequestBody SignupForm signupForm,
            HttpServletResponse response
    ) {
        return authService.register(signupForm, response);
    }

    @Operation(
            summary = "Log in a user",
            description = "Authenticates a user by email and password, then returns an access token. A refresh token is written to the response cookies."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully.",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponseForm.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Login form validation failed.",
                    content = @Content(
                            schema = @Schema(implementation = ValidationErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication failed.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponseForm login(
            @Valid @RequestBody LoginForm loginForm,
            HttpServletResponse response
    ){
        return authService.login(loginForm, response);
    }

    @Operation(
            summary = "Log out a user",
            description = "Deletes the refresh token cookie from the response."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User logged out successfully.",
                    content = @Content
            )
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response){
        authService.logout(request, response);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Creates a new access token using the refresh token from the request cookies."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Access token refreshed successfully.",
                    content = @Content(
                            schema = @Schema(implementation = AuthResponseForm.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request cookies were not found.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Refresh token is missing, invalid, expired, or belongs to a non-existing user.",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AuthResponseForm refreshToken(HttpServletRequest request){
        return authService.refreshToken(request);
    }
}
