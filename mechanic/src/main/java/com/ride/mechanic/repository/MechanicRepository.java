package com.ride.mechanic.repository;

import com.ride.mechanic.dto.AvailabilityStatus;
import com.ride.mechanic.entity.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MechanicRepository extends JpaRepository<Mechanic, Long> {

    Optional<Mechanic> findByEmail(String email);

    List<Mechanic> findByAvailabilityAndVerifiedTrue(
            AvailabilityStatus availability
    );
}
