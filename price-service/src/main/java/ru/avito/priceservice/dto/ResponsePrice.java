package ru.avito.priceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponsePrice(
        @JsonProperty("price") Long price,
        @JsonProperty("location_id") Long locationId,
        @JsonProperty("microcategory_id") Long microCategoryId,
        @JsonProperty("matrix_id") Long matrixId,
        @JsonProperty("user_segment_id") Long userSegmentId
) {
}
