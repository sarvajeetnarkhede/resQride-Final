package com.ride.mechanic.entity;

import com.ride.mechanic.dto.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "centers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Center {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    private Double latitude;

    private Double longitude;

    private String city;

    private String state;

    private String contactPhone;

    private String contactEmail;

    @Enumerated(EnumType.STRING)
    private CenterStatus status;

    @Column(name = "max_mechanics_per_skill")
    @Builder.Default
    private Integer maxMechanicsPerSkill = 1; // Default: 1 mechanic per skill type

    public enum CenterStatus {
        ACTIVE, INACTIVE, MAINTENANCE
    }
}
