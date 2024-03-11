package ru.avito.priceservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 32)
    private String name;
    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id", nullable = true)
    private Category category;
}
