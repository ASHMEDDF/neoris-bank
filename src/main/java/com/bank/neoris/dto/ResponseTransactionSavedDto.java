package com.bank.neoris.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class ResponseTransactionSavedDto {

    LocalDate date;
    String transactionType;
    double transactionValue;
    double balance;
}
