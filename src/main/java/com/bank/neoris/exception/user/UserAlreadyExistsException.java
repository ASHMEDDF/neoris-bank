package com.bank.neoris.exception.user;

import java.io.Serial;

public class UserAlreadyExistsException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 2L;

    public UserAlreadyExistsException(Long id) {
        super("Client with ID " + id + " already exists");
    }
}
