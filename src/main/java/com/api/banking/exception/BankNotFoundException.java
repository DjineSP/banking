package com.api.banking.exception;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(Long id) {
        super("Banque introuvable avec l'id : " + id);
    }
    public BankNotFoundException(String code) {
        super("Banque introuvable avec le code : " + code);
    }
}
