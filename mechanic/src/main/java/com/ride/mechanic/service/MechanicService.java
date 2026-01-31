package com.ride.mechanic.service;

import lombok.RequiredArgsConstructor;
import com.ride.mechanic.dto.*;
import com.ride.mechanic.entity.Mechanic;
import com.ride.mechanic.repository.MechanicRepository;
import com.ride.mechanic.client.ServiceRequestClient;
import com.ride.mechanic.client.UserServiceClient;
import com.ride.mechanic.client.FeedbackClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MechanicService {

    private final MechanicRepository repository;
    private final ServiceRequestClient serviceRequestClient;
    private final UserServiceClient userServiceClient;
    private final FeedbackClient feedbackClient;

    public Mono<MechanicResponseDTO> register(MechanicCreateDTO dto) {

        // First create user with MECHANIC role
        UserCreateRequest userRequest = new UserCreateRequest();
        userRequest.setFullName(dto.getName());
        userRequest.setEmail(dto.getEmail());
        userRequest.setPhoneNo(dto.getPhone());
        userRequest.setPassword(dto.getPassword());
        userRequest.setRole("MECHANIC");

        return userServiceClient.createUser(userRequest)
                .flatMap(user -> {
                    // Then create mechanic record
                    Mechanic mechanic = Mechanic.builder()
                            .email(dto.getEmail())
                            .name(dto.getName())
                            .phone(dto.getPhone())
                            .skillType(dto.getSkillType())
                            .availability(AvailabilityStatus.OFFLINE)
                            .rating(0.0)
                            .verified(false)
                            .build();

                    return Mono.just(map(repository.save(mechanic)));
                })
                .onErrorResume(e -> {
                    // If user creation fails, we should not create the mechanic record
                    return Mono.error(new RuntimeException("Failed to create user record: " + e.getMessage(), e));
                });
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

    public List<MechanicResponseDTO> getAllMechanics() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public List<Object> getMyRequests(String email) {
        try {
            System.out.println("Getting requests for mechanic email: " + email);
            Mechanic mechanic = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Mechanic not found with email: " + email));
            System.out.println("Found mechanic with ID: " + mechanic.getId());
            
            List<Object> requests = serviceRequestClient.getMyRequests(mechanic.getId());
            System.out.println("Retrieved requests: " + requests.size());
            return requests;
        } catch (Exception e) {
            System.err.println("Error getting mechanic requests: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get mechanic requests: " + e.getMessage(), e);
        }
    }

    public String getMechanicName(Long id) {
        return repository.findById(id)
                .map(Mechanic::getName)
                .orElse("Unknown Mechanic");
    }

    public String getMechanicEmail(Long id) {
        return repository.findById(id)
                .map(Mechanic::getEmail)
                .orElse("unknown@example.com");
    }

    public MechanicResponseDTO getMechanicByEmail(String email) {
        Mechanic mechanic = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Mechanic not found with email: " + email));
        return map(mechanic);
    }

    public void updateRating(Long id, Double newRating) {
        Mechanic mechanic = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mechanic not found with ID: " + id));
        
        mechanic.setRating(newRating);
        repository.save(mechanic);
    }

    public void updateAvailabilityById(Long id, AvailabilityStatus status) {
        Mechanic mechanic = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mechanic not found with ID: " + id));
        
        mechanic.setAvailability(status);
        repository.save(mechanic);
    }

    public MechanicResponseDTO getMechanicById(Long id) {
        Mechanic mechanic = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mechanic not found with ID: " + id));
        return map(mechanic);
    }

    private MechanicResponseDTO map(Mechanic m) {
        Double overallRating = 0.0;
        
        try {
            // Fetch overall rating from feedback service
            overallRating = feedbackClient.getAverageRating(m.getId()).block();
        } catch (Exception e) {
            // Fallback to existing rating if feedback service is unavailable
            overallRating = m.getRating();
        }

        return MechanicResponseDTO.builder()
                .id(m.getId())
                .email(m.getEmail())
                .name(m.getName())
                .phone(m.getPhone())
                .skillType(m.getSkillType())
                .rating(overallRating) // Use overall rating from feedback service
                .availability(m.getAvailability())
                .verified(m.isVerified())
                .build();
    }
}
