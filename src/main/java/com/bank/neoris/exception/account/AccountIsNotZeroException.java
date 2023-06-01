package com.bank.neoris.exception.account;

import java.io.Serial;
import java.util.List;
import java.util.stream.Collectors;

public class AccountIsNotZeroException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 4L;

    public AccountIsNotZeroException(Long id, List<Long> accounts) {
        super("Client with ID " + id + " has money in the account(s) " + accounts.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", ")));
    }

}
