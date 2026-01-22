package com.ride.servicerequest.dto;

public enum ServiceStatus {

    CREATED,        // request created
    PAYMENT_PENDING,
    PAID,           // payment confirmed
    ASSIGNED,       // mechanic assigned
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
