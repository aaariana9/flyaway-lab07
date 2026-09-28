package com.flyaway.flightbooking.booking.domain;

import java.time.LocalDateTime;

public record BookingCreatedEvent(
        Long bookingId,
        String customerFirstName,
        String customerLastName,
        String flightNumber,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        LocalDateTime bookingDate
) {
}
