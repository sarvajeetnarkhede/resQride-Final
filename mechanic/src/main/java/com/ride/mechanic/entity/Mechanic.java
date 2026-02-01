package com.ride.mechanic.entity;

import com.ride.mechanic.dto.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mechanics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String phone;

    private String skillType;

    private Double rating;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availability;

    private boolean verified;

    // Center assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "center_id")
    private Center assignedCenter;

    // Note: Individual location fields removed - service location is now center-based
    // Mechanics operate from their assigned center location
}
