package com.ride.servicerequest.repository;

import com.ride.servicerequest.entity.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRequestRepository
        extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByUserEmail(String userEmail);
}
