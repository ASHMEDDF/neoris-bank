package com.bank.neoris.controller;

import com.bank.neoris.domain.Account;
import com.bank.neoris.enumerations.AccountTypes;
import com.bank.neoris.service.AccountService;
import com.bank.neoris.service.ClientService;
import com.bank.neoris.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void testCreateAccount_Success() throws Exception {
        Account createAccountRequest = new Account();
        createAccountRequest.setAccountType(AccountTypes.AHORROS);
        createAccountRequest.setInitialBalance(1000.0);
        createAccountRequest.setState(true);
        createAccountRequest.setClientIdentification(123456788L);

        Account savedAccount = new Account();
        savedAccount.setAccountNumber(123456789L);
        createAccountRequest.setAccountType(AccountTypes.AHORROS);
        savedAccount.setInitialBalance(1000.0);
        createAccountRequest.setState(true);
        savedAccount.setClientIdentification(123456788L);

        when(accountService.saveAccount(any(Account.class))).thenReturn(savedAccount);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createAccountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(savedAccount.getAccountNumber()))
                .andExpect(jsonPath("$.accountType").value(savedAccount.getAccountType()))
                .andExpect(jsonPath("$.initialBalance").value(savedAccount.getInitialBalance()))
                .andExpect(jsonPath("$.clientIdentification").value(savedAccount.getClientIdentification()))
                .andExpect(jsonPath("$.state").value(savedAccount.getState()));


        verify(accountService, times(1)).saveAccount(any(Account.class));
    }

    @Test
    void testGetAccountByClientId_Success() throws Exception {
        Long clientId = 123456788L;

        Account account1 = new Account();
        account1.setAccountNumber(123456789L);
        account1.setAccountType(AccountTypes.AHORROS);
        account1.setInitialBalance(1000.0);
        account1.setState(true);
        account1.setClientIdentification(clientId);

        Account account2 = new Account();
        account2.setAccountNumber(987654321L);
        account2.setAccountType(AccountTypes.AHORROS);
        account2.setInitialBalance(2000.0);
        account2.setState(true);
        account2.setClientIdentification(clientId);

        List<Account> accountsList = Arrays.asList(account1, account2);

        when(accountService.getAccountsByClientId(anyLong())).thenReturn(accountsList);

        mockMvc.perform(get("/cuentas/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountList", hasSize(2)))
                .andExpect(jsonPath("$.accountList[0].accountNumber").value(account1.getAccountNumber()))
                .andExpect(jsonPath("$.accountList[0].accountType").value(account1.getAccountType().toString()))
                .andExpect(jsonPath("$.accountList[0].initialBalance").value(account1.getInitialBalance()))
                .andExpect(jsonPath("$.accountList[0].state").value(account1.getState()))
                .andExpect(jsonPath("$.accountList[0].clientIdentification").value(account1.getClientIdentification()))
                .andExpect(jsonPath("$.accountList[1].accountNumber").value(account2.getAccountNumber()))
                .andExpect(jsonPath("$.accountList[1].accountType").value(account2.getAccountType().toString()))
                .andExpect(jsonPath("$.accountList[1].initialBalance").value(account2.getInitialBalance()))
                .andExpect(jsonPath("$.accountList[1].state").value(account2.getState()))
                .andExpect(jsonPath("$.accountList[1].clientIdentification").value(account2.getClientIdentification()));

        verify(accountService, times(1)).getAccountsByClientId(clientId);
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
