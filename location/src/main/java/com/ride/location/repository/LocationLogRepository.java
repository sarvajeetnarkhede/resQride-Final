package com.ride.location.repository;

import com.ride.location.entity.LocationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationLogRepository
        extends JpaRepository<LocationLog, Long> {

    List<LocationLog> findByUserEmailOrderByTimestampDesc(String userEmail);
}
