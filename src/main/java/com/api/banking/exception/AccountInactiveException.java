package com.api.banking.exception;

import com.api.banking.enums.AccountStatus;

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(Long id, AccountStatus status) {
        super("Le compte " + id + " est " + (status == AccountStatus.SUSPENDED ? "suspendu" : "clôturé"));
    }
    public AccountInactiveException(Long id) {
        super("Le compte " + id + " est désactivé");
    }
}
