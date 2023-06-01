package com.bank.neoris.exception.account;

import java.io.Serial;

public class AccountNotExistsException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 6L;

    public AccountNotExistsException(Long id) {
        super("The account with number: " + id + " does not exist");
    }
}
