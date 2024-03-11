package ru.avito.priceservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record Storage(
        @JsonProperty("baseline") String baseline,
        @JsonProperty("discounts") Map<Long, String> discounts
) {
}
