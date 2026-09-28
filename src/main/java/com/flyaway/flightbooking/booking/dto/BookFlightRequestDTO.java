package com.flyaway.flightbooking.booking.dto;

import jakarta.validation.constraints.NotNull;

public record BookFlightRequestDTO(
        @NotNull(message = "flightId is required")
        Long flightId
) {
}
