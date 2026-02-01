package com.ride.mechanic.dto;

import lombok.Data;

@Data
public class CenterCreateDTO {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String contactPhone;
    private String contactEmail;
    private Integer maxMechanicsPerSkill = 1;
}
