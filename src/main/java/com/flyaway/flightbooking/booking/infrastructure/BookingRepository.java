package com.flyaway.flightbooking.booking.infrastructure;

import com.flyaway.flightbooking.booking.domain.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Two intervals overlap when each one starts before the other ends
    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.customer.id = :customerId
              AND b.flight.departureTime < :arrivalTime
              AND b.flight.arrivalTime > :departureTime
            """)
    boolean existsOverlappingBooking(@Param("customerId") Long customerId,
                                     @Param("departureTime") LocalDateTime departureTime,
                                     @Param("arrivalTime") LocalDateTime arrivalTime);

    @EntityGraph(attributePaths = {"flight", "customer"})
    Optional<Booking> findWithDetailsById(Long id);
}
