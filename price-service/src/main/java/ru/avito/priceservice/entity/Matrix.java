package ru.avito.priceservice.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Matrix {
    private Long microCategoryId;
    private Long locationId;
    private Long price;
}
