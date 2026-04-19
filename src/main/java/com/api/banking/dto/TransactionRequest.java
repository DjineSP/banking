package com.api.banking.dto;

import java.math.BigDecimal;

public record TransactionRequest(BigDecimal amount) {}
