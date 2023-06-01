package com.bank.neoris.domain;

import com.bank.neoris.enumerations.AccountTypes;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_number", unique = true)
    private Long accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountTypes accountType;
    private double initialBalance;
    private Boolean state;

    @NotNull
    private Long clientIdentification;


}
