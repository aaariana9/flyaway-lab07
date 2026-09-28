package com.flyaway.flightbooking.booking.application;

import com.flyaway.flightbooking.booking.domain.BookingService;
import com.flyaway.flightbooking.booking.dto.BookFlightRequestDTO;
import com.flyaway.flightbooking.booking.dto.BookingResponseDTO;
import com.flyaway.flightbooking.user.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Protected - customer data is taken from the JWT, not from the body
    @PostMapping("/flights/book")
    public ResponseEntity<BookingResponseDTO> book(@Valid @RequestBody BookFlightRequestDTO request,
                                                   @AuthenticationPrincipal User customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.book(request.flightId(), customer));
    }

    // Protected - path is /flight/book/{id} (singular) as required by the lab
    @GetMapping("/flight/book/{id}")
    public ResponseEntity<BookingResponseDTO> findById(@PathVariable Long id,
                                                       @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(bookingService.findById(id, customer));
    }
}
