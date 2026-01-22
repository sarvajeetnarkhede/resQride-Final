package org.ride.user.controller;

import lombok.RequiredArgsConstructor;
import org.ride.user.dto.InternalUserAuthResponse;
import org.ride.user.dto.UserCreateRequest;
import org.ride.user.dto.UserResponse;
import org.ride.user.model.User;
import org.ride.user.repository.UserRepository;
import org.ride.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping("/auth/{email}")
    public InternalUserAuthResponse getUserForAuth(@PathVariable String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        InternalUserAuthResponse dto = new InternalUserAuthResponse();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setRole(String.valueOf(user.getRole()));
        dto.setFullName(user.getFullName());

        return dto;
    }
}
