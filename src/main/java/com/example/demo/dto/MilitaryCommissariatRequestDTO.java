package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MilitaryCommissariatRequestDTO {
    @NotBlank(message = "Фамилия обязательна")
    private String surname;

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Отчество обязательно")
    private String patronymic;

    @NotBlank(message = "Улица адреса обязательна")
    private String addressStreet;

    @NotNull(message = "Номер здания обязателен")
    private Integer addressBuilding;

    @NotBlank(message = "Номер телефона обязателен")
    private String phoneNumber;
}
