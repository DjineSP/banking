package com.api.banking.dto;

public record CreateAccountRequest(
        String fullname,
        String phone,
        String email
) {}
