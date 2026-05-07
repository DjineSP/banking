package com.api.banking.dto.request;

public record CreateAccountRequest(
        Long bankId,
        String fullname,
        String phone,
        String email
) {}
