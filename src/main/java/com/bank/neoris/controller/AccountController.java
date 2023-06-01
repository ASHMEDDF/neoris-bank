package com.bank.neoris.controller;

import com.bank.neoris.domain.Account;
import com.bank.neoris.dto.*;
import com.bank.neoris.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class AccountController {

    private AccountService accountService;

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
        List<Account> accountsOptional = accountService.getAccountsByClientId(clientId);

        ResponseAccountsByClientIdDto responseAccountsByClientIdDto = new ResponseAccountsByClientIdDto(
                accountsOptional
        );
        return ResponseEntity.ok(responseAccountsByClientIdDto);
    }

}
