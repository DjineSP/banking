package com.api.banking.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String field, String value) {
        super("Un compte avec ce " + field + " existe déjà : " + value);
    }
}
