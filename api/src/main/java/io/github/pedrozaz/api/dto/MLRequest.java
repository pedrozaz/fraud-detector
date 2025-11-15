package io.github.pedrozaz.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MLRequest(
        double amount,
        @JsonProperty("tx_hour") int txHour,
        @JsonProperty("device_score") int deviceScore
) {
}
