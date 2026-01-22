package com.ride.payment.controller;

import com.ride.payment.dto.*;
import com.ride.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public PaymentResponseDTO pay(
            Authentication auth,
            @RequestBody PaymentCreateDTO dto
    ) {
        return service.pay(auth.getName(), dto);
    }
}
