package com.ride.mechanic.service;

import lombok.RequiredArgsConstructor;
import com.ride.mechanic.dto.*;
import com.ride.mechanic.entity.Mechanic;
import com.ride.mechanic.repository.MechanicRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MechanicService {

    private final MechanicRepository repository;

    public MechanicResponseDTO register(MechanicCreateDTO dto) {

        Mechanic mechanic = Mechanic.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .phone(dto.getPhone())
                .skillType(dto.getSkillType())
                .availability(AvailabilityStatus.OFFLINE)
                .rating(0.0)
                .verified(false)
                .build();

        return map(repository.save(mechanic));
    }

    public void verify(Long id) {
        Mechanic mechanic = repository.findById(id).orElseThrow();
        mechanic.setVerified(true);
        repository.save(mechanic);
    }

    public void updateAvailability(String email, AvailabilityStatus status) {
        Mechanic mechanic = repository.findByEmail(email).orElseThrow();
        mechanic.setAvailability(status);
        repository.save(mechanic);
    }

    public List<MechanicResponseDTO> available() {
        return repository
                .findByAvailabilityAndVerifiedTrue(AvailabilityStatus.AVAILABLE)
                .stream()
                .map(this::map)
                .toList();
    }

    private MechanicResponseDTO map(Mechanic m) {
        return MechanicResponseDTO.builder()
                .id(m.getId())
                .email(m.getEmail())
                .name(m.getName())
                .phone(m.getPhone())
                .skillType(m.getSkillType())
                .rating(m.getRating())
                .availability(m.getAvailability())
                .verified(m.isVerified())
                .build();
    }
}
