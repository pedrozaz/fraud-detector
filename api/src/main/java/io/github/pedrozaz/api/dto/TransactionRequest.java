package io.github.pedrozaz.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        String userId,
        BigDecimal amount,
        String currency,
        String merchantId,
        LocalDateTime timestamp,
        String cardId,
        int deviceScore
) {
}
