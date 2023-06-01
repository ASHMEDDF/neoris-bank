package com.bank.neoris.exception.account;

import java.io.Serial;

public class AccountsNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 4L;

    public AccountsNotFoundException(Long id) {
        super("Client with ID " + id + " has no registered accounts");
    }
}