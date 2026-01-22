package org.ride.user.service;

import org.ride.user.dto.UserCreateRequest;
import org.ride.user.dto.UserResponse;

import java.util.List;

public interface UserService {


    UserResponse getUserByEmail(String email);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateProfile(String email, UserCreateRequest request);

    UserResponse deleteUser(String email);

    List<UserResponse> getAllUsers();
}
