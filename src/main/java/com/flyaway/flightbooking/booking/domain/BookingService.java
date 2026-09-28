package com.flyaway.flightbooking.booking.domain;

import com.flyaway.flightbooking.booking.dto.BookingResponseDTO;
import com.flyaway.flightbooking.booking.infrastructure.BookingRepository;
import com.flyaway.flightbooking.exception.BadRequestException;
import com.flyaway.flightbooking.exception.ConflictException;
import com.flyaway.flightbooking.exception.NotFoundException;
import com.flyaway.flightbooking.flight.domain.Flight;
import com.flyaway.flightbooking.flight.infrastructure.FlightRepository;
import com.flyaway.flightbooking.user.domain.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository,
                          ApplicationEventPublisher eventPublisher, Clock clock) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public BookingResponseDTO book(Long flightId, User customer) {
        // Row lock: concurrent bookings of the same flight wait here, so seats can't go negative
        Flight flight = flightRepository.findByIdForUpdate(flightId)
                .orElseThrow(() -> new NotFoundException("Flight not found: " + flightId));

        LocalDateTime now = LocalDateTime.now(clock);
        if (!flight.getDepartureTime().isAfter(now)) {
            throw new BadRequestException(flight.getArrivalTime().isAfter(now)
                    ? "Cannot book a flight that is in transit"
                    : "Cannot book a flight that has already departed");
        }

        if (flight.getAvailableSeats() <= 0) {
            throw new ConflictException("Flight " + flight.getFlightNumber() + " has no available seats");
        }

        if (bookingRepository.existsOverlappingBooking(customer.getId(),
                flight.getDepartureTime(), flight.getArrivalTime())) {
            throw new ConflictException("You already have a booking that overlaps with flight " + flight.getFlightNumber());
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - 1);

        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setCustomer(customer);
        booking.setCustomerFirstName(customer.getFirstName());
        booking.setCustomerLastName(customer.getLastName());
        booking.setBookingDate(now.truncatedTo(ChronoUnit.SECONDS));
        booking = bookingRepository.save(booking);

        // Handled after commit, so the email is only written for bookings that were actually saved
        eventPublisher.publishEvent(new BookingCreatedEvent(
                booking.getId(),
                booking.getCustomerFirstName(),
                booking.getCustomerLastName(),
                flight.getFlightNumber(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                booking.getBookingDate()
        ));

        return BookingResponseDTO.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingResponseDTO findById(Long bookingId, User requester) {
        Booking booking = bookingRepository.findWithDetailsById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!booking.getCustomer().getId().equals(requester.getId())) {
            throw new AccessDeniedException("You can only view your own bookings");
        }

        return BookingResponseDTO.from(booking);
    }
}
