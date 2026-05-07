package com.api.banking.exception;

public class BankAlreadyExistsException extends RuntimeException {
    public BankAlreadyExistsException(String code) {
        super("Une banque avec ce code existe déjà : " + code);
    }
}
