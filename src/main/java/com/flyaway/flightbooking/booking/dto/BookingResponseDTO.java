package com.flyaway.flightbooking.booking.dto;

import com.flyaway.flightbooking.booking.domain.Booking;

import java.time.LocalDateTime;

public record BookingResponseDTO(
        Long id,
        Long customerId,
        String customerFirstName,
        String customerLastName,
        LocalDateTime bookingDate,
        Long flightId,
        String flightNumber,
        String airline,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime
) {
    public static BookingResponseDTO from(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCustomerFirstName(),
                booking.getCustomerLastName(),
                booking.getBookingDate(),
                booking.getFlight().getId(),
                booking.getFlight().getFlightNumber(),
                booking.getFlight().getAirline(),
                booking.getFlight().getDepartureTime(),
                booking.getFlight().getArrivalTime()
        );
    }
}
