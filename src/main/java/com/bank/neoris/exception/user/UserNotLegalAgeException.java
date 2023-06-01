package com.bank.neoris.exception.user;

import java.io.Serial;

public class UserNotLegalAgeException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 3L;

    public UserNotLegalAgeException() {
        super("The customer is under the minimum age limit");
    }

}
