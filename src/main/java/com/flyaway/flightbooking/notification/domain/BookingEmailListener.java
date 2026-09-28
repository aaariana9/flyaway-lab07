package com.flyaway.flightbooking.notification.domain;

import com.flyaway.flightbooking.booking.domain.BookingCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BookingEmailListener {

    private static final Logger log = LoggerFactory.getLogger(BookingEmailListener.class);

    private final Path outputDir;

    public BookingEmailListener(@Value("${booking.email.output-dir}") String outputDir) {
        this.outputDir = Path.of(outputDir);
    }

    // AFTER_COMMIT: only runs if the booking transaction succeeded. @Async: doesn't slow down the HTTP response
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookingCreated(BookingCreatedEvent event) {
        Path file = outputDir.resolve("flight_booking_email_" + event.bookingId() + ".txt");
        try {
            Files.createDirectories(outputDir);
            Files.writeString(file, buildEmail(event));
            log.info("Booking confirmation email written to {}", file.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not write booking confirmation email {}", file.toAbsolutePath(), e);
        }
    }

    private String buildEmail(BookingCreatedEvent event) {
        return """
                To: %s %s
                Subject: Fly Away Travel - Booking confirmation #%d

                Hello %s %s,

                Your booking has been confirmed.

                Booking ID:     %d
                Flight number:  %s
                Departure:      %s
                Arrival:        %s
                Booking date:   %s

                Thank you for flying with Fly Away Travel!
                """.formatted(
                event.customerFirstName(), event.customerLastName(), event.bookingId(),
                event.customerFirstName(), event.customerLastName(),
                event.bookingId(),
                event.flightNumber(),
                iso(event.departureTime()),
                iso(event.arrivalTime()),
                iso(event.bookingDate()));
    }

    private static String iso(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
