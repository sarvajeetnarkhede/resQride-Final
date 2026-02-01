package com.ride.mechanic.service;

import lombok.RequiredArgsConstructor;
import com.ride.mechanic.dto.*;
import com.ride.mechanic.entity.Center;
import com.ride.mechanic.entity.Mechanic;
import com.ride.mechanic.repository.CenterRepository;
import com.ride.mechanic.repository.MechanicRepository;
import com.ride.mechanic.client.ServiceRequestClient;
import com.ride.mechanic.client.UserServiceClient;
import com.ride.mechanic.client.FeedbackClient;
import com.ride.mechanic.util.SkillMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MechanicService {

    private final MechanicRepository repository;
    private final CenterRepository centerRepository;
    private final ServiceRequestClient serviceRequestClient;
    private final UserServiceClient userServiceClient;
    private final FeedbackClient feedbackClient;
    private final DistanceService distanceService;
    private final CenterService centerService;

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
            // Fetch overall rating from feedback service with timeout
            overallRating = feedbackClient.getAverageRating(m.getId())
                    .timeout(java.time.Duration.ofSeconds(3))
                    .onErrorReturn(m.getRating())
                    .block();
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
                .assignedCenter(m.getAssignedCenter() != null ? 
                    mapCenterToDTO(m.getAssignedCenter()) : null)
                .build();
    }

    public List<MechanicResponseDTO> availableBySkill(String skill) {
        return repository
                .findByAvailabilityAndVerifiedTrueAndSkillType(AvailabilityStatus.AVAILABLE, skill)
                .stream()
                .map(this::map)
                .toList();
    }

    /**
     * Get available mechanics by skill and calculate distance from user location
     * @param skill Mechanic skill type
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @return List of mechanics with distance information, sorted by distance
     */
    public List<MechanicResponseDTO> availableBySkillWithDistance(String skill, Double userLatitude, Double userLongitude) {
        List<MechanicResponseDTO> mechanics = repository
                .findByAvailabilityAndVerifiedTrueAndSkillType(AvailabilityStatus.AVAILABLE, skill)
                .stream()
                .map(this::map)
                .toList();
        
        return distanceService.addDistanceToMechanics(mechanics, userLatitude, userLongitude);
    }

    /**
     * Get all available mechanics with distance from user location
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @return List of mechanics with distance information, sorted by distance
     */
    public List<MechanicResponseDTO> availableWithDistance(Double userLatitude, Double userLongitude) {
        List<MechanicResponseDTO> mechanics = repository
                .findByAvailabilityAndVerifiedTrue(AvailabilityStatus.AVAILABLE)
                .stream()
                .map(this::map)
                .toList();
        
        return distanceService.addDistanceToMechanics(mechanics, userLatitude, userLongitude);
    }

    /**
     * Get nearest mechanics within a specific radius
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @param maxDistance Maximum distance in km
     * @return List of mechanics within the specified radius
     */
    public List<MechanicResponseDTO> nearestMechanics(Double userLatitude, Double userLongitude, Double maxDistance) {
        List<MechanicResponseDTO> mechanics = availableWithDistance(userLatitude, userLongitude);
        
        if (maxDistance != null) {
            return distanceService.filterByDistance(mechanics, maxDistance);
        }
        
        return mechanics;
    }

    /**
     * Note: Individual location tracking removed
     * Mechanics now operate from their assigned center location
     * Use center-based distance calculation methods instead
     */

    /**
     * Assign mechanic to a center
     * @param email Mechanic's email
     * @param centerId Center ID
     */
    public void assignToCenter(String email, Long centerId) {
        Mechanic mechanic = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Mechanic not found"));
        
        Center center = centerRepository.findById(centerId)
                .orElseThrow(() -> new RuntimeException("Center not found"));
        
        // Check if center has available slot for this skill type
        if (!centerService.hasAvailableSlot(centerId, mechanic.getSkillType())) {
            throw new RuntimeException("Center has no available slot for skill type: " + mechanic.getSkillType());
        }
        
        mechanic.setAssignedCenter(center);
        repository.save(mechanic);
    }

    /**
     * Get available mechanics by skill with center-based distance calculation
     * @param skill Mechanic skill type
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @return List of mechanics with distance information, sorted by distance
     */
    public List<MechanicResponseDTO> availableBySkillWithCenterDistance(String skill, Double userLatitude, Double userLongitude) {
        List<MechanicResponseDTO> mechanics = repository
                .findByAvailabilityAndVerifiedTrueAndSkillType(AvailabilityStatus.AVAILABLE, skill)
                .stream()
                .map(this::map)
                .toList();
        
        // Use center location for distance calculation
        return distanceService.addDistanceToMechanicsWithCenterLocation(mechanics, userLatitude, userLongitude);
    }

    /**
     * Get nearest mechanics by center location
     * @param userLatitude User's latitude
     * @param userLongitude User's longitude
     * @param maxDistance Maximum distance in km
     * @return List of mechanics within the specified radius
     */
    public List<MechanicResponseDTO> nearestMechanicsByCenter(Double userLatitude, Double userLongitude, Double maxDistance) {
        List<MechanicResponseDTO> mechanics = repository
                .findByAvailabilityAndVerifiedTrue(AvailabilityStatus.AVAILABLE)
                .stream()
                .map(this::map)
                .toList();
        
        // Use center location for distance calculation
        List<MechanicResponseDTO> mechanicsWithDistance = 
            distanceService.addDistanceToMechanicsWithCenterLocation(mechanics, userLatitude, userLongitude);
        
        if (maxDistance != null) {
            return distanceService.filterByDistance(mechanicsWithDistance, maxDistance);
        }
        
        return mechanicsWithDistance;
    }

    private CenterDTO mapCenterToDTO(Center center) {
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
