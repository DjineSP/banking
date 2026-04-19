package com.api.banking.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Solde insuffisant pour effectuer cette opération");
    }
}
