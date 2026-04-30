package com.api.banking.dto;

import java.math.BigDecimal;

public record TransferRequest(Long targetAccountId, BigDecimal amount) {}
