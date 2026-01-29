package com.ride.mechanic.dto;

import lombok.Data;

@Data
public class MechanicCreateDTO {
    private String email;
    private String name;
    private String phone;
    private String skillType;
    private String password;
}
