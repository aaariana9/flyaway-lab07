package com.flyaway.flightbooking.flight.domain;

import com.flyaway.flightbooking.exception.ConflictException;
import com.flyaway.flightbooking.flight.dto.CreateFlightRequest;
import com.flyaway.flightbooking.flight.dto.FlightResponse;
import com.flyaway.flightbooking.flight.infrastructure.FlightRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public FlightResponse create(CreateFlightRequest request) {
        if (flightRepository.existsByFlightNumber(request.flightNumber())) {
            throw new ConflictException("Flight number already exists: " + request.flightNumber());
        }

        Flight flight = new Flight();
        flight.setFlightNumber(request.flightNumber());
        flight.setAirline(request.airline());
        flight.setDepartureTime(request.departureTime());
        flight.setArrivalTime(request.arrivalTime());
        flight.setAvailableSeats(request.availableSeats());

        return FlightResponse.from(flightRepository.save(flight));
    }

    public List<FlightResponse> search(String flightNumber, String airline,
                                       LocalDateTime departureFrom, LocalDateTime departureTo) {
        Specification<Flight> spec = (root, query, cb) -> cb.conjunction();

        if (flightNumber != null && !flightNumber.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.upper(root.get("flightNumber")), "%" + flightNumber.toUpperCase() + "%"));
        }
        if (airline != null && !airline.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.upper(root.get("airline")), "%" + airline.toUpperCase() + "%"));
        }
        if (departureFrom != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("departureTime"), departureFrom));
        }
        if (departureTo != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("departureTime"), departureTo));
        }

        return flightRepository.findAll(spec).stream()
                .map(FlightResponse::from)
                .toList();
    }
}
