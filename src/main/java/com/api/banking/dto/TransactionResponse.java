package com.api.banking.dto;

import com.api.banking.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"idTransaction", "accountId", "type", "amount", "status", "newBalance", "createdAt", "updatedAt"})
public record TransactionResponse(
        Long idTransaction,
        Long accountId,
        Transaction.TransactionType type,
        BigDecimal amount,
        Transaction.TransactionStatus status,
        BigDecimal newBalance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction tx, BigDecimal newBalance) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAccountId(),
                tx.getType(),
                tx.getAmount(),
                tx.getStatus(),
                newBalance,
                tx.getCreatedAt(),
                tx.getUpdatedAt()
        );
    }
}
