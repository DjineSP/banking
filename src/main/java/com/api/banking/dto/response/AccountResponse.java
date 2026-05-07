package com.api.banking.dto.response;

import com.api.banking.entity.Account;
import com.api.banking.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "accountNumber", "fullname", "phone", "email", "balance", "status", "bankCode", "bankName", "createdAt"})
public record AccountResponse(
        Long id,
        String accountNumber,
        String fullname,
        String phone,
        String email,
        BigDecimal balance,
        AccountStatus status,
        String bankCode,
        String bankName,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getFullname(),
                account.getPhone(),
                account.getEmail(),
                account.getBalance(),
                account.getStatus(),
                account.getBank().getCode(),
                account.getBank().getName(),
                account.getCreatedAt()
        );
    }
}
