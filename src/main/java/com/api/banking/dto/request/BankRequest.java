package com.api.banking.dto.request;

import java.math.BigDecimal;

public record BankRequest(
        String code,
        String name,
        BigDecimal debitFeeRate,
        BigDecimal debitFeeFlat,
        BigDecimal intraFeeRate,
        BigDecimal intraFeeFlat,
        BigDecimal interFeeRate,
        BigDecimal interFeeFlat
) {}
