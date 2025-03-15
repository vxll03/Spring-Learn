package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "military_commissariat")
public class MilitaryCommissariat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String surname;
    @Column(nullable = false)
    String name;
    @Column(nullable = false)
    String patronymic;

    @Column(nullable = false)
    String addressStreet;
    @Column(nullable = false)
    Integer addressBuilding;

    @Column(nullable = false)
    String phoneNumber;

    @Lob
    @Column(nullable = true)
    private byte[] photo;
}
