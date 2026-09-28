package com.flyaway.flightbooking.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRegisterRequestDTO(
        @NotBlank(message = "firstName is required")
        @Pattern(regexp = ".*[A-Z].*", message = "firstName must contain at least one uppercase letter")
        String firstName,

        @NotBlank(message = "lastName is required")
        @Pattern(regexp = ".*[A-Z].*", message = "lastName must contain at least one uppercase letter")
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        String email,

        @NotBlank(message = "password is required")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "password must have at least 8 characters, 1 letter and 1 number")
        String password
) {
}
