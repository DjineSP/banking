package com.api.banking.dto;

import com.api.banking.entity.Transaction;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"idTransaction", "accountId", "type", "amount", "status", "balanceAfter", "linkedAccountId", "createdAt", "updatedAt"})
public record TransactionResponse(
        Long idTransaction,
        Long accountId,
        Transaction.TransactionType type,
        BigDecimal amount,
        Transaction.TransactionStatus status,
        BigDecimal balanceAfter,
        Long linkedAccountId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction tx, BigDecimal balanceAfter) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAccountId(),
                tx.getType(),
                tx.getAmount(),
                tx.getStatus(),
                balanceAfter,
                tx.getLinkedAccountId(),
                tx.getCreatedAt(),
                tx.getUpdatedAt()
        );
    }

    public static TransactionResponse from(Transaction tx) {
        return from(tx, tx.getBalanceAfter());
    }
}
