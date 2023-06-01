package com.bank.neoris.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class ResponseReportByAccountDto {

    LocalDate date;
    String client;
    Long accountNumber;
    String type;
    double initialBalance;
    boolean state;
    double transaction;
    double availableBalance;

}
