package com.api.banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "banks")
@Getter @Setter @NoArgsConstructor
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "debit_fee_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal debitFeeRate = BigDecimal.ZERO;

    @Column(name = "debit_fee_flat", nullable = false, precision = 10, scale = 2)
    private BigDecimal debitFeeFlat = BigDecimal.ZERO;

    @Column(name = "intra_fee_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal intraFeeRate = BigDecimal.ZERO;

    @Column(name = "intra_fee_flat", nullable = false, precision = 10, scale = 2)
    private BigDecimal intraFeeFlat = BigDecimal.ZERO;

    @Column(name = "inter_fee_rate", nullable = false, precision = 8, scale = 4)
    private BigDecimal interFeeRate = BigDecimal.ZERO;

    @Column(name = "inter_fee_flat", nullable = false, precision = 10, scale = 2)
    private BigDecimal interFeeFlat = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
