package com.api.banking.dto.request;

import java.math.BigDecimal;

public record TransactionRequest(BigDecimal amount) {}
