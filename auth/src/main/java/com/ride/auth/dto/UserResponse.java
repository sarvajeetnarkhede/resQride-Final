package com.ride.auth.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String password;
    private String role;
}
