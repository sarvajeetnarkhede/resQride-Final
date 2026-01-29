package com.ride.auth.controller;

import com.ride.auth.client.UserServiceClient;
import com.ride.auth.dto.AuthRequest;
import com.ride.auth.dto.AuthResponse;
import com.ride.auth.dto.UserRegistrationRequest;
import com.ride.auth.dto.UserResponse;
import com.ride.auth.producer.AuthEventPublisher;
import com.ride.auth.security.CustomUserDetailsService;
import com.ride.auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomUserDetailsService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceClient userServiceClient;
    private final AuthEventPublisher authEventPublisher;


    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest request) {

        return authService.authenticate(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = jwtTokenProvider.generateToken(
                            user.getEmail(),
                            user.getFullName(),
                            user.getRole()
                    );

                    authEventPublisher.userLoggedIn(user.getEmail());

                    return new AuthResponse(token, "Bearer");
                })
                .onErrorResume(e ->
                        Mono.error(new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                e.getMessage()
                        ))
                );
    }

    @PostMapping("/register")
    public Mono<UserResponse> register(@RequestBody UserRegistrationRequest request) {

        return userServiceClient.createUser(request)
                .doOnNext(user ->
                        authEventPublisher.userRegistered(user.getEmail())
                )
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.CONFLICT) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "User with this email already exists. Please use a different email or try logging in."
                        ));
                    }
                    return Mono.error(e);
                })
                .onErrorResume(ResponseStatusException.class, e -> {
                    return Mono.error(e);
                })
                .onErrorResume(e -> {
                    return Mono.error(new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Registration failed: " + e.getMessage()
                    ));
                });
    }
}
