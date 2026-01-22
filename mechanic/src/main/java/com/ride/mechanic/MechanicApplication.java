package com.ride.mechanic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MechanicApplication {

    public static void main(String[] args) {
        SpringApplication.run(MechanicApplication.class, args);
    }

}
