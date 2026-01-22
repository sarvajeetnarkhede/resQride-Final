package org.ride.user.dto;

import lombok.Builder;
import lombok.Data;
import org.ride.user.model.Role;

import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNo;
    private Role role;
    private List<AddressDTO> addresses;
}
