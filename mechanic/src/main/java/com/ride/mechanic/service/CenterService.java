package com.ride.mechanic.service;

import com.ride.mechanic.dto.CenterCreateDTO;
import com.ride.mechanic.dto.CenterDTO;
import com.ride.mechanic.entity.Center;
import com.ride.mechanic.repository.CenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CenterService {

    private final CenterRepository centerRepository;

    public CenterDTO createCenter(CenterCreateDTO dto) {
        Center center = Center.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .city(dto.getCity())
                .state(dto.getState())
                .contactPhone(dto.getContactPhone())
                .contactEmail(dto.getContactEmail())
                .status(Center.CenterStatus.ACTIVE)
                .maxMechanicsPerSkill(dto.getMaxMechanicsPerSkill() != null ? dto.getMaxMechanicsPerSkill() : 1)
                .build();

        Center savedCenter = centerRepository.save(center);
        return mapToDTO(savedCenter);
    }

    public List<CenterDTO> getAllActiveCenters() {
        return centerRepository.findActiveCenters()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CenterDTO> getActiveCentersByCity(String city) {
        return centerRepository.findActiveCentersByCity(city)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CenterDTO> getCentersWithAvailableSlot(String skillType) {
        return centerRepository.findCentersWithAvailableSlot(skillType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CenterDTO getCenterById(Long id) {
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Center not found with id: " + id));
        return mapToDTO(center);
    }

    public boolean hasAvailableSlot(Long centerId, String skillType) {
        Integer currentCount = centerRepository.countMechanicsBySkillAndCenter(centerId, skillType);
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new RuntimeException("Center not found with id: " + centerId));
        return currentCount < center.getMaxMechanicsPerSkill();
    }

    public CenterDTO mapToDTO(Center center) {
        return CenterDTO.builder()
                .id(center.getId())
                .name(center.getName())
                .address(center.getAddress())
                .latitude(center.getLatitude())
                .longitude(center.getLongitude())
                .city(center.getCity())
                .state(center.getState())
                .contactPhone(center.getContactPhone())
                .contactEmail(center.getContactEmail())
                .status(center.getStatus())
                .maxMechanicsPerSkill(center.getMaxMechanicsPerSkill())
                .build();
    }
}
