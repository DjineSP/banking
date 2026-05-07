package com.api.banking.exception;

public class BankInactiveException extends RuntimeException {
    public BankInactiveException(String code) {
        super("La banque " + code + " est désactivée");
    }
}
