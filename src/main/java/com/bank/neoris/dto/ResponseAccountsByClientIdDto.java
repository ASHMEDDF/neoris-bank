package com.bank.neoris.dto;

import com.bank.neoris.domain.Account;
import lombok.Value;

import java.util.List;

@Value
public class ResponseAccountsByClientIdDto {
    List<Account> accountList;
}
