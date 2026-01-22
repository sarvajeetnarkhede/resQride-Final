package org.ride.user.controller;


import lombok.RequiredArgsConstructor;
import org.ride.user.dto.UserCreateRequest;
import org.ride.user.dto.UserResponse;
import org.ride.user.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/email/{email}")
	public UserResponse getUserByEmail(@PathVariable String email) {
		return userService.getUserByEmail(email);
	}

	@GetMapping("/me")
	public UserResponse me(Authentication auth) {
		String email = auth.getName();
		return userService.getUserByEmail(email);
	}

	@PutMapping("/me")
	public UserResponse update(Authentication auth,
	@RequestBody UserCreateRequest request) {
		String email = auth.getName();
		return userService.updateProfile(email, request);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getAllUsers() {
		return userService.getAllUsers();
	}

	@DeleteMapping
	public UserResponse deleteUsers(Authentication auth) {
		String email = auth.getName();
		return userService.deleteUser(email);
	}

}
