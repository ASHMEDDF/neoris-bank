package com.bank.neoris.exception.transaction;

import java.io.Serial;

public class TransactionNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 8L;

    public TransactionNotFoundException() {
        super("Transaction not found");
    }
}
