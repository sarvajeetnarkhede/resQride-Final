package com.ride.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore fields we don't have in auth service
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private Object role; // Use Object to handle both String and Role enum
    private List<Object> addresses; // Use Object since we don't have AddressDTO in auth service
}
