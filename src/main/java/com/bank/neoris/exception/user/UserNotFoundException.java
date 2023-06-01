package com.bank.neoris.exception.user;


import java.io.Serial;

public class UserNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(Long id) {
        super("Client with ID " + id + " not found");
    }
}
