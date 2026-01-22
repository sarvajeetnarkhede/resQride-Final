package org.ride.user.service;

import lombok.RequiredArgsConstructor;
import org.ride.user.dto.AddressDTO;
import org.ride.user.model.Address;
import org.ride.user.model.User;
import org.ride.user.repository.AddressRepository;
import org.ride.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    // ================= READ =================

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByUser(String email) {

        User user = getUser(email);

        return user.getAddresses()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ================= CREATE =================

    @Override
    public AddressDTO addAddress(String email, AddressDTO dto) {

        User user = getUser(email);

        Address address = new Address();
        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country());
        address.setPincode(dto.pincode());
        address.setUser(user);

        Address saved = addressRepository.save(address);
        return toDTO(saved);
    }

    // ================= UPDATE =================

    @Override
    public AddressDTO updateAddress(String email, Long addressId, AddressDTO dto) {

        Address address = getOwnedAddress(email, addressId);

        address.setCity(dto.city());
        address.setState(dto.state());
        address.setCountry(dto.country());
        address.setPincode(dto.pincode());

        return toDTO(address);
    }

    // ================= DELETE =================

    @Override
    public void deleteAddress(String email, Long addressId) {

        Address address = getOwnedAddress(email, addressId);
        addressRepository.delete(address);
    }

    // ================= HELPERS =================

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    private Address getOwnedAddress(String email, Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Address not found"
                ));

        if (!address.getUser().getEmail().equals(email)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Address does not belong to user"
            );
        }

        return address;
    }

    private AddressDTO toDTO(Address address) {
        return new AddressDTO(
                address.getAddressId(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPincode()
        );
    }
}
