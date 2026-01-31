package com.ride.mechanic.dto;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private String role;
}
