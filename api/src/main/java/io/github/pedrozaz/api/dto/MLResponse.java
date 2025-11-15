package io.github.pedrozaz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MLResponse(
        @JsonProperty("isFraud") boolean isFraud,
        @JsonProperty("fraudScore") double fraudScore
) {
}
