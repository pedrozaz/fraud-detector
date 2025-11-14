package io.github.pedrozaz.api.dto;

import io.github.pedrozaz.api.model.StatusEnum;

public record FraudCheckResponse(
        String transactionId,
        StatusEnum status,
        boolean isFraud,
        double fraudScore
) {
}
