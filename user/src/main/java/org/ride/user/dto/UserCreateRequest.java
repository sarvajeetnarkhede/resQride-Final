package org.ride.user.dto;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String fullName;
    private String email;
    private String phoneNo;
    private String password;
}
