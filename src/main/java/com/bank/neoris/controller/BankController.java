package com.bank.neoris.controller;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.domain.Transaction;
import com.bank.neoris.dto.*;
import com.bank.neoris.exception.account.AccountsNotFoundException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.service.AccountService;
import com.bank.neoris.service.BankService;
import com.bank.neoris.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class BankController {

    private BankService bankService;
    private AccountService accountService;
    private TransactionService transactionService;

    @PostMapping("clientes")
    public ResponseEntity<ResponseClientSavedDto> createClient (@RequestBody @Valid Client createClientRequest) {

        Client saveNewClient = bankService.saveNewClient(createClientRequest);
        return new ResponseEntity<>(new ResponseClientSavedDto(saveNewClient.getName(),
                saveNewClient.getAddress(),
                saveNewClient.getPhone(),
                saveNewClient.getPassword(),
                saveNewClient.getState()),
                HttpStatus.OK);
    }

    @GetMapping("clientes/{clientId}")
    public ResponseEntity<ResponseClientByIdDto> getClientById(@PathVariable Long clientId) {
        Client clientOptional = bankService.getByClientId(clientId)
                .orElseThrow(() -> new UserNotFoundException(clientId));

        ResponseClientByIdDto loanResponseDto = new ResponseClientByIdDto(clientOptional.getIdentification(),
                clientOptional.getName(),
                clientOptional.getGender(),
                clientOptional.getAge(),
                clientOptional.getAddress(),
                clientOptional.getPhone(),
                clientOptional.getPassword(),
                clientOptional.getState());
        return ResponseEntity.ok(loanResponseDto);

    }

    @DeleteMapping("clientes/{clientId}")
    public ResponseEntity<String> deleteClientById(@PathVariable Long clientId) {

        boolean deleted = bankService.deleteByClientId(clientId);

        if (deleted) {
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("clientes")
    public ResponseEntity<ResponseClientSavedDto> updateClient (@RequestBody @Valid Client createClientRequest) {

        Client saveNewClient = bankService.updateClient(createClientRequest);
        return new ResponseEntity<>(new ResponseClientSavedDto(saveNewClient.getName(),
                saveNewClient.getAddress(),
                saveNewClient.getPhone(),
                saveNewClient.getPassword(),
                saveNewClient.getState()),
                HttpStatus.OK);
    }

    @PostMapping("cuentas")
    public ResponseEntity<ResponseAccountSavedDto> createAccount (@RequestBody @Valid Account createAccountRequest) {

        Account saveAccount = accountService.saveAccount(createAccountRequest);
        return new ResponseEntity<>(new ResponseAccountSavedDto(saveAccount.getAccountNumber(),
                saveAccount.getAccountType(),
                saveAccount.getInitialBalance(),
                saveAccount.getState(),
                saveAccount.getClientIdentification()),
                HttpStatus.OK);

    }

    @GetMapping("cuentas/{clientId}")
    public ResponseEntity<ResponseAccountsByClientIdDto> getAccountByClientId(@PathVariable Long clientId) {
        List<Account> accountsOptional = accountService.getAccountsByClientId(clientId)
                .orElseThrow(() -> new AccountsNotFoundException(clientId));

        ResponseAccountsByClientIdDto responseAccountsByClientIdDto = new ResponseAccountsByClientIdDto(
                accountsOptional
        );
        return ResponseEntity.ok(responseAccountsByClientIdDto);
    }

    @PostMapping("movimientos")
    public ResponseEntity<ResponseTransactionSavedDto> createTransaction (@RequestBody @Valid CreateTransactionRequestDto createTransactionRequest) {

        Transaction saveTransaction = transactionService.saveTransaction(createTransactionRequest);

        return new ResponseEntity<>(new ResponseTransactionSavedDto(saveTransaction.getDate(),
                saveTransaction.getTransactionType(),
                saveTransaction.getTransactionValue(),
                saveTransaction.getFinalBalance()),
                HttpStatus.OK);

    }

    @GetMapping("movimientos/reportes")
    public ResponseEntity<List<ResponseReportByAccountDto>> generateReportStatusAccount(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate initialDate,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam("cliente") Long clientId
    ) {
        List<ResponseReportByAccountDto> report = transactionService.generateReportStatusAccount(initialDate, endDate, clientId);
        return ResponseEntity.ok(report);
    }


}
