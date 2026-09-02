package com.zest.product.config;

import com.zest.product.entity.Role;
import com.zest.product.entity.User;
import com.zest.product.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeUsers() {

        return args -> {

            // Create USER
            if (!userRepository.existsByUsername("user")) {

                User user = User.builder()
                        .username("user")
                        .email("user@gmail.com")
                        .password(passwordEncoder.encode("user123"))
                        .role(Role.USER)
                        .build();

                userRepository.save(user);
            }

            // Create ADMIN
            if (!userRepository.existsByUsername("admin")) {

                User admin = User.builder()
                        .username("admin")
                        .email("admin@gmail.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);
            }
        };
    }
}