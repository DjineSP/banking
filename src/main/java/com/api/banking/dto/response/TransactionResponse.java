package com.api.banking.dto.response;

import com.api.banking.entity.Transaction;
import com.api.banking.enums.FeeType;
import com.api.banking.enums.TransactionStatus;
import com.api.banking.enums.TransactionType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "accountId", "type", "amount", "fee", "feeType", "status",
        "balanceAfter", "linkedAccountNumber", "sourceBankId", "targetBankId", "createdAt", "updatedAt"})
public record TransactionResponse(
        Long id,
        Long accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal fee,
        FeeType feeType,
        TransactionStatus status,
        BigDecimal balanceAfter,
        String linkedAccountNumber,
        Long sourceBankId,
        Long targetBankId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAccountId(),
                tx.getType(),
                tx.getAmount(),
                tx.getFee(),
                tx.getFeeType(),
                tx.getStatus(),
                tx.getBalanceAfter(),
                tx.getLinkedAccountNumber(),
                tx.getSourceBankId(),
                tx.getTargetBankId(),
                tx.getCreatedAt(),
                tx.getUpdatedAt()
        );
    }
}
