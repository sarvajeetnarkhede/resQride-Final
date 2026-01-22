package org.ride.user.service;

import org.ride.user.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    List<AddressDTO> getAddressesByUser(String email);

    AddressDTO addAddress(String email, AddressDTO dto);

    AddressDTO updateAddress(String email, Long addressId, AddressDTO dto);

    void deleteAddress(String email, Long addressId);
}
