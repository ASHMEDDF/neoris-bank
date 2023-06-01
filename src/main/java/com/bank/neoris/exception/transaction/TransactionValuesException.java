package com.bank.neoris.exception.transaction;

import java.io.Serial;

public class TransactionValuesException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 4L;

    public TransactionValuesException() {
        super("Insufficient balance");
    }
}
