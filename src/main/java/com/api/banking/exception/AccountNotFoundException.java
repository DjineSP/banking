package com.api.banking.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Compte introuvable avec l'id : " + id);
    }
}
