package com.flyaway.flightbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

@Configuration
@EnableAsync
public class AppConfig {

    // Injected instead of calling LocalDateTime.now() directly, so "now" can be fixed in tests
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    // Lives here (not in SecurityConfig) to avoid a cycle: SecurityConfig -> JWT filter -> UserService -> PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
