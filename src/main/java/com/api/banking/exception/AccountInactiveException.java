package com.api.banking.exception;

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(Long id) {
        super("Le compte " + id + " est désactivé");
    }
}
