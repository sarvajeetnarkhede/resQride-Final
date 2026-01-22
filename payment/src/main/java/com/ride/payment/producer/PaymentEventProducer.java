package com.ride.payment.producer;

import com.ride.payment.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(PaymentEvent event) {
        kafkaTemplate.send(
                "payment-events",
                event.getRequestId().toString(),
                event
        );
    }
}
