package com.bank.neoris.dto;

import lombok.Value;

@Value
public class ResponseClientSavedDto {

    String name;
    String address;
    Long phone;
    int password;
    Boolean state;
}
