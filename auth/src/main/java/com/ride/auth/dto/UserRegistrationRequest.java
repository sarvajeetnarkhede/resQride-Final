package com.ride.auth.dto;

import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String fullName;
    private String email;
    private String phoneNo;
    private String password;
}
