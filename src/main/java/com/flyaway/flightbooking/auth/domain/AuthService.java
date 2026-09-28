package com.flyaway.flightbooking.auth.domain;

import com.flyaway.flightbooking.auth.JWTService;
import com.flyaway.flightbooking.auth.dto.LoginRequestDTO;
import com.flyaway.flightbooking.auth.dto.LoginResponseDTO;
import com.flyaway.flightbooking.exception.NotFoundException;
import com.flyaway.flightbooking.exception.UnauthorizedException;
import com.flyaway.flightbooking.user.domain.User;
import com.flyaway.flightbooking.user.infrastructure.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("No account found for email: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Incorrect password");
        }

        return new LoginResponseDTO(jwtService.generateToken(user));
    }
}
