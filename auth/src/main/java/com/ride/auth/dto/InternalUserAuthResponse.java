package com.ride.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalUserAuthResponse {
    private Long id;
    private String email;
    private String password;
    private String role;
    private String fullName;
}
