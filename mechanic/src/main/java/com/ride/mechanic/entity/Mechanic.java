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
}
