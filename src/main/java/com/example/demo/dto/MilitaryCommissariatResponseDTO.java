package com.example.demo.dto;

import lombok.Data;

@Data
public class MilitaryCommissariatResponseDTO {
    private String surname;
    private String name;
    private String patronymic;
    private String addressStreet;
    private Integer addressBuilding;
    private String phoneNumber;
}
