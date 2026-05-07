package com.api.banking.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(Long id) {
        super("Compte introuvable avec l'id : " + id);
    }
    public AccountNotFoundException(String accountNumber) {
        super("Compte introuvable avec le numéro : " + accountNumber);
    }
}
