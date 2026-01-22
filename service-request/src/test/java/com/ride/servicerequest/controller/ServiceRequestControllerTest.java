package com.ride.servicerequest.controller;

import com.ride.servicerequest.dto.ServiceRequestResponseDTO;
import com.ride.servicerequest.service.ServiceRequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServiceRequestControllerTest {

    @Mock
    private ServiceRequestService service;

    @Mock
    private Authentication auth;

    @InjectMocks
    private ServiceRequestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void myRequests_WhenAuthenticated_ReturnsList() {
        // Arrange
        String email = "test@example.com";
        when(auth.getName()).thenReturn(email);
        when(service.getMyRequests(email)).thenReturn(List.of(
                ServiceRequestResponseDTO.builder().requestId(1L).build()
        ));

        // Act
        List<ServiceRequestResponseDTO> result = controller.myRequests(auth);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(service).getMyRequests(email);
    }

    @Test
    void myRequests_WhenNotAuthenticated_ThrowsUnauthorized() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.myRequests(null);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Missing authentication credentials"));
    }

    @Test
    void myRequests_WhenAuthNameIsNull_ThrowsUnauthorized() {
        // Arrange
        when(auth.getName()).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            controller.myRequests(auth);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }
}
