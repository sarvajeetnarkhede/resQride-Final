package com.ride.servicerequest.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ride.servicerequest.service.ServiceRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final ServiceRequestService service;
    private final ObjectMapper mapper;

    @KafkaListener(
            topics = "payment-events",
            groupId = "service-request-group"
    )
    public void consume(byte[] payload) {

        try {
            PaymentEvent event =
                    mapper.readValue(payload, PaymentEvent.class);

            if ("PAYMENT_SUCCESS".equals(event.getEventType())) {
                service.markPaid(event.getRequestId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
