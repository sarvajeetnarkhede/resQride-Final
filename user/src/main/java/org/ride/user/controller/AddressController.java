package org.ride.user.controller;

import lombok.RequiredArgsConstructor;
import org.ride.user.dto.AddressDTO;
import org.ride.user.service.AddressService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public List<AddressDTO> getMyAddresses(Authentication auth) {
        String email = auth.getName();
        return addressService.getAddressesByUser(email);
    }

    @PostMapping
    public AddressDTO addAddress(
            Authentication auth,
            @RequestBody AddressDTO dto
    ) {
        String email = auth.getName();
        return addressService.addAddress(email, dto);
    }

    @PutMapping("/{addressId}")
    public AddressDTO updateAddress(
            Authentication auth,
            @PathVariable Long addressId,
            @RequestBody AddressDTO dto
    ) {
        String email = auth.getName();
        return addressService.updateAddress(email, addressId, dto);
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(
            Authentication auth,
            @PathVariable Long addressId
    ) {
        String email = auth.getName();
        addressService.deleteAddress(email, addressId);
    }
}
