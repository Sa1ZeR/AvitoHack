package ru.avito.priceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RequestPrice(
        @JsonProperty("location_id") Long locationId,
        @JsonProperty("microcategory_id") Long microCategoryId,
        @JsonProperty("user_id") Long userId
) {
}
