package com.bank.neoris.dto;

import com.bank.neoris.enumerations.AccountTypes;
import lombok.Value;

@Value
public class ResponseAccountSavedDto {

    Long accountNumber;
    AccountTypes accountType;
    double initialBalance;
    Boolean state;
    Long clientIdentification;
}
