package com.api.banking.dto.response;

import com.api.banking.entity.Bank;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "code", "name", "isActive", "debitFeeRate", "debitFeeFlat",
        "intraFeeRate", "intraFeeFlat", "interFeeRate", "interFeeFlat", "createdAt"})
public record BankResponse(
        Long id,
        String code,
        String name,
        boolean isActive,
        BigDecimal debitFeeRate,
        BigDecimal debitFeeFlat,
        BigDecimal intraFeeRate,
        BigDecimal intraFeeFlat,
        BigDecimal interFeeRate,
        BigDecimal interFeeFlat,
        LocalDateTime createdAt
) {
    public static BankResponse from(Bank bank) {
        return new BankResponse(
                bank.getId(),
                bank.getCode(),
                bank.getName(),
                bank.isActive(),
                bank.getDebitFeeRate(),
                bank.getDebitFeeFlat(),
                bank.getIntraFeeRate(),
                bank.getIntraFeeFlat(),
                bank.getInterFeeRate(),
                bank.getInterFeeFlat(),
                bank.getCreatedAt()
        );
    }
}
