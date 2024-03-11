package ru.avito.priceservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "matrix")
public class MapMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 18, unique = true)
    private String name;
}
