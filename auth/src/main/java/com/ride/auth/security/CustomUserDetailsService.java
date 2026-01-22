package com.ride.auth.security;

import com.ride.auth.client.UserServiceClient;
import com.ride.auth.dto.InternalUserAuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;

    public Mono<InternalUserAuthResponse> authenticate(String email, String rawPassword) {

        return userServiceClient.getUserForAuth(email)
                .flatMap(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                    return Mono.just(user);
                });
    }
}
