package com.ride.mechanic.dto;

import com.ride.mechanic.entity.Center;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CenterDTO {
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String city;
    private String state;
    private String contactPhone;
    private String contactEmail;
    private Center.CenterStatus status;
    private Integer maxMechanicsPerSkill;
}
