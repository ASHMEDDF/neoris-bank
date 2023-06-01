package com.bank.neoris.dto;

import lombok.Value;

@Value
public class ResponseClientByIdDto {

    Long identification;

    String name;
    String gender;
    int age;
    String address;
    Long phone;
    int password;
    Boolean state;

}
