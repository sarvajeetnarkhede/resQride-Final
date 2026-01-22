package com.ride.location.service;

import lombok.RequiredArgsConstructor;
import com.ride.location.dto.*;
import com.ride.location.entity.LocationLog;
import com.ride.location.repository.LocationLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationLogRepository repository;

    public LocationResponseDTO log(String email, LocationCreateDTO dto) {

        LocationLog log = LocationLog.builder()
                .userEmail(email)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .timestamp(LocalDateTime.now())
                .build();

        return map(repository.save(log));
    }

    public List<LocationResponseDTO> history(String email) {
        return repository
                .findByUserEmailOrderByTimestampDesc(email)
                .stream()
                .map(this::map)
                .toList();
    }

    private LocationResponseDTO map(LocationLog log) {
        return LocationResponseDTO.builder()
                .id(log.getId())
                .userEmail(log.getUserEmail())
                .latitude(log.getLatitude())
                .longitude(log.getLongitude())
                .timestamp(log.getTimestamp())
                .build();
    }
}
