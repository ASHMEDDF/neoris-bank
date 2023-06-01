package com.bank.neoris.dto;

import com.bank.neoris.enumerations.TransactionTypes;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequestDto {

    @Enumerated(EnumType.STRING)
    private TransactionTypes transactionType;

    private double transactionValue;
    private Long accountNumber;

}
