package io.github.batorfly.task_tracker_backend.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@GroupSequence({SignupForm.class, SignupForm.NotBlankGroup.class, SignupForm.SizeGroup.class,  SignupForm.PatternGroup.class})
public record SignupForm(
        @JsonProperty("first_name")
        @Schema(
                description = "User first name. Must contain only Latin letters.",
                example = "John",
                minLength = 3,
                maxLength = 64,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "First name cannot be empty", groups = NotBlankGroup.class)
        @Size(min = 3, max = 64, message = "First name must be between 3 and 64 characters", groups = SizeGroup.class)
        @Pattern(regexp = "^[A-Za-z]+$", message = "First name must include only letters", groups = PatternGroup.class)
        String firstName,

        @JsonProperty("last_name")
        @Schema(
                description = "User last name. Must contain only Latin letters.",
                example = "Smith",
                minLength = 3,
                maxLength = 64,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Last name cannot be empty", groups = NotBlankGroup.class)
        @Size(min = 3, max = 64, message = "Last name must be between 3 and 64 characters", groups = SizeGroup.class)
        @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must include only letters", groups = PatternGroup.class)
        String lastName,

        @Schema(
                description = "User email address.",
                example = "john.smith@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Email cannot be empty", groups = NotBlankGroup.class)
        @Email(message = "Invalid email", groups = PatternGroup.class)
        String email,

        @Schema(
                description = "User password. Must be at least 8 characters and contain uppercase and lowercase letters.",
                example = "StrongPass123",
                minLength = 8,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Password cannot be empty", groups = NotBlankGroup.class)
        @Size(min = 8, message = "Password must be 8 characters or more", groups = SizeGroup.class)
        @Pattern(
                regexp = "(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "Password must contain at least one uppercase and one lowercase letter",
                groups = PatternGroup.class
        )
        String password
        ) {
    interface NotBlankGroup{}
    interface SizeGroup{}
    interface PatternGroup{}
}
