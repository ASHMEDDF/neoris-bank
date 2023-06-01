package com.bank.neoris.exception.account;

import java.io.Serial;

public class AccountIsNotZeroException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 4L;

    public AccountIsNotZeroException(Long id) {
        super("Client with ID " + id + " has money in one or more accounts");
    }

}
