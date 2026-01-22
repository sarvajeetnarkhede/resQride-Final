package org.ride.user.dto;

import lombok.Data;

@Data
public class InternalUserAuthResponse {
    private Long id;
    private String email;
    private String password;
    private String role;
    private String fullName;
}
