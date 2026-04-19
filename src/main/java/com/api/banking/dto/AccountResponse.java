package com.api.banking.dto;

import com.api.banking.entity.Account;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonPropertyOrder({"idAccount", "fullname", "phone", "email", "balance", "isActive", "createdAt"})
public record AccountResponse(
        Long idAccount,
        String fullname,
        String phone,
        String email,
        BigDecimal balance,
        boolean isActive,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getFullname(),
                account.getPhone(),
                account.getEmail(),
                account.getBalance(),
                account.isActive(),
                account.getCreatedAt()
        );
    }
}
