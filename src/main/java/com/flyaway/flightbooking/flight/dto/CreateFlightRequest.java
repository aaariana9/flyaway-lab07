package com.flyaway.flightbooking.flight.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateFlightRequest(
        @NotBlank(message = "flightNumber is required")
        @Pattern(regexp = "^[A-Z0-9]{1,6}$", message = "flightNumber must contain only A-Z and 0-9, max 6 characters")
        String flightNumber,

        @NotBlank(message = "airline is required")
        String airline,

        @NotNull(message = "departureTime is required")
        LocalDateTime departureTime,

        @NotNull(message = "arrivalTime is required")
        LocalDateTime arrivalTime,

        @NotNull(message = "availableSeats is required")
        @Positive(message = "availableSeats must be greater than 0")
        Integer availableSeats
) {
    @AssertTrue(message = "departureTime must be before arrivalTime")
    public boolean isDepartureBeforeArrival() {
        if (departureTime == null || arrivalTime == null) {
            return true;
        }
        return departureTime.isBefore(arrivalTime);
    }
}
