package org.ride.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressDTO(

        Long addressId,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Country is required")
        String country,

        @NotBlank(message = "Pincode is required")
        @Size(min = 4, max = 10, message = "Invalid pincode")
        String pincode
) {
}
