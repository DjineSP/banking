package com.api.banking.dto.request;

import java.math.BigDecimal;

public record InterBankTransferRequest(
        String targetBankCode,
        String targetAccountNumber,
        BigDecimal amount
) {}
