package com.api.banking.dto.request;

import java.math.BigDecimal;

public record TransferRequest(String targetAccountNumber, BigDecimal amount) {}
