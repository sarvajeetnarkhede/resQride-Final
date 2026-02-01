package com.ride.mechanic.controller;

import com.ride.mechanic.dto.CenterCreateDTO;
import com.ride.mechanic.dto.CenterDTO;
import com.ride.mechanic.service.CenterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centers")
@RequiredArgsConstructor
public class CenterController {

    private final CenterService centerService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CenterDTO createCenter(@RequestBody CenterCreateDTO dto) {
        return centerService.createCenter(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CenterDTO> getAllActiveCenters() {
        return centerService.getAllActiveCenters();
    }

    @GetMapping("/city/{city}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CenterDTO> getActiveCentersByCity(@PathVariable String city) {
        return centerService.getActiveCentersByCity(city);
    }

    @GetMapping("/available-slots/{skillType}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CenterDTO> getCentersWithAvailableSlot(@PathVariable String skillType) {
        return centerService.getCentersWithAvailableSlot(skillType);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CenterDTO getCenterById(@PathVariable Long id) {
        return centerService.getCenterById(id);
    }

    @GetMapping("/{centerId}/has-slot/{skillType}")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean hasAvailableSlot(@PathVariable Long centerId, @PathVariable String skillType) {
        return centerService.hasAvailableSlot(centerId, skillType);
    }
}
