package io.github.pedrozaz.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String transactionId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private BigDecimal amount;

    private String currency;
    private String merchantId;
    private String cardId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private Integer deviceScore;

    @Column(nullable = false)
    private boolean isFraud;

    @Column(nullable = false)
    private double fraudScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;
}
