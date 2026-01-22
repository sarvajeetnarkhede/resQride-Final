package org.ride.user.service;

import lombok.RequiredArgsConstructor;
import org.ride.user.dto.AddressDTO;
import org.ride.user.dto.UserCreateRequest;
import org.ride.user.dto.UserResponse;
import org.ride.user.model.Role;
import org.ride.user.model.User;
import org.ride.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
        return mapToResponse(user);
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhoneNo(request.getPhoneNo());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        return mapToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateProfile(String email, UserCreateRequest request) {
        User user = getUser(email);

        user.setFullName(request.getFullName());
        user.setPhoneNo(request.getPhoneNo());

        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse deleteUser(String email) {
        User user = getUser(email);
        userRepository.delete(user);
        return null;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .role(user.getRole())
                .addresses(
                        user.getAddresses() == null
                                ? List.of()
                                : user.getAddresses().stream()
                                .map(a -> new AddressDTO(
                                        a.getAddressId(),
                                        a.getCity(),
                                        a.getState(),
                                        a.getCountry(),
                                        a.getPincode()))
                                .toList()
                )
                .build();
    }

}
