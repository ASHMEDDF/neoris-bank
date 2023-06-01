package com.bank.neoris.controller;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.domain.Transaction;
import com.bank.neoris.dto.CreateTransactionRequestDto;
import com.bank.neoris.dto.ResponseReportByAccountDto;
import com.bank.neoris.enumerations.AccountTypes;
import com.bank.neoris.enumerations.TransactionTypes;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.service.AccountService;
import com.bank.neoris.service.BankService;
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

import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class BankControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankService bankService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private TransactionService transactionService;

    @Test
    void testCreateClient_Success() throws Exception {
        Client createClientRequest = new Client();
        createClientRequest.setName("John Doe");
        createClientRequest.setAddress("123 Main St");
        createClientRequest.setPhone(5551234L);
        createClientRequest.setPassword(1234);
        createClientRequest.setState(true);

        Client savedClient = new Client();
        savedClient.setIdentification(1L);
        savedClient.setName(createClientRequest.getName());
        savedClient.setAddress(createClientRequest.getAddress());
        savedClient.setPhone(createClientRequest.getPhone());
        savedClient.setPassword(createClientRequest.getPassword());
        savedClient.setState(createClientRequest.getState());
        when(bankService.saveNewClient(any(Client.class))).thenReturn(savedClient);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(savedClient.getName()))
                .andExpect(jsonPath("$.address").value(savedClient.getAddress()))
                .andExpect(jsonPath("$.phone").value(savedClient.getPhone()))
                .andExpect(jsonPath("$.password").value(savedClient.getPassword()))
                .andExpect(jsonPath("$.state").value(savedClient.getState()));

        verify(bankService, times(1)).saveNewClient(any(Client.class));
    }

    @Test
    void testGetClientById_Success() throws Exception {
        Long clientId = 12345L;

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("John Doe");
        client.setGender("Male");
        client.setAge(30);
        client.setAddress("123 Main St");
        client.setPhone(123456L);
        client.setPassword(1245);
        client.setState(true);

        when(bankService.getByClientId(clientId)).thenReturn(Optional.of(client));

        mockMvc.perform(get("/clientes/{clientId}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identification").value(client.getIdentification()))
                .andExpect(jsonPath("$.name").value(client.getName()))
                .andExpect(jsonPath("$.gender").value(client.getGender()))
                .andExpect(jsonPath("$.age").value(client.getAge()))
                .andExpect(jsonPath("$.address").value(client.getAddress()))
                .andExpect(jsonPath("$.phone").value(client.getPhone()))
                .andExpect(jsonPath("$.password").value(client.getPassword()))
                .andExpect(jsonPath("$.state").value(client.getState()));

        verify(bankService, times(1)).getByClientId(clientId);
    }

    @Test
    void testDeleteClientById_ClientFound() throws Exception {
        Long clientId = 12345L;

        when(bankService.deleteByClientId(clientId)).thenReturn(true);

        mockMvc.perform(delete("/clientes/{clientId}", clientId))
                .andExpect(status().isAccepted());

        verify(bankService, times(1)).deleteByClientId(clientId);
    }

    @Test
    void testDeleteClientById_ClientNotFound() throws Exception {
        Long clientId = 12345L;

        when(bankService.deleteByClientId(clientId)).thenReturn(false);

        mockMvc.perform(delete("/clientes/{clientId}", clientId))
                .andExpect(status().isNotFound());

        verify(bankService, times(1)).deleteByClientId(clientId);
    }

    @Test
    void testUpdateClient_ClientFound() throws Exception {
        Client createClientRequest = new Client();
        createClientRequest.setName("John Doe");
        createClientRequest.setAddress("123 Main St");
        createClientRequest.setPhone(123456L);
        createClientRequest.setPassword(1245);
        createClientRequest.setState(true);

        Client updatedClient = new Client();
        updatedClient.setName("John Doe");
        updatedClient.setAddress("456 Elm St");
        updatedClient.setPhone(654321L);
        updatedClient.setPassword(9876);
        updatedClient.setState(false);

        when(bankService.updateClient(any(Client.class))).thenReturn(updatedClient);

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedClient.getName()))
                .andExpect(jsonPath("$.address").value(updatedClient.getAddress()))
                .andExpect(jsonPath("$.phone").value(updatedClient.getPhone()))
                .andExpect(jsonPath("$.password").value(updatedClient.getPassword()))
                .andExpect(jsonPath("$.state").value(updatedClient.getState()));

        verify(bankService, times(1)).updateClient(any(Client.class));
    }

    @Test
    void testUpdateClient_ClientNotFound() throws Exception {
        Client createClientRequest = new Client();
        createClientRequest.setIdentification(1L);
        createClientRequest.setName("John Doe");
        createClientRequest.setAddress("123 Main St");
        createClientRequest.setPhone(123456L);
        createClientRequest.setPassword(1245);
        createClientRequest.setState(true);

        when(bankService.updateClient(any(Client.class))).thenThrow(new UserNotFoundException(createClientRequest.getIdentification()));

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isNotFound());

        verify(bankService, times(1)).updateClient(any(Client.class));
    }

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

        when(accountService.getAccountsByClientId(anyLong())).thenReturn(Optional.of(accountsList));

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

    @Test
    void testCreateTransaction_Success() throws Exception {
        CreateTransactionRequestDto createTransactionRequest = new CreateTransactionRequestDto();
        createTransactionRequest.setAccountNumber(1L);
        createTransactionRequest.setTransactionType(TransactionTypes.CREDITO);
        createTransactionRequest.setTransactionValue(500.0);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setTransactionType(createTransactionRequest.getTransactionType().toString());
        savedTransaction.setTransactionValue(createTransactionRequest.getTransactionValue());
        savedTransaction.setFinalBalance(1500.0);

        when(transactionService.saveTransaction(any(CreateTransactionRequestDto.class)))
                .thenReturn(savedTransaction);

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createTransactionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionType").value(savedTransaction.getTransactionType()))
                .andExpect(jsonPath("$.transactionValue").value(savedTransaction.getTransactionValue()));

        verify(transactionService, times(1)).saveTransaction(createTransactionRequest);
    }

    @Test
    void testGenerateReportStatusAccount_Success() throws Exception {
        LocalDate initialDate = LocalDate.of(2023, 5, 1);
        LocalDate endDate = LocalDate.of(2023, 5, 31);
        Long clientId = 123456788L;

        List<ResponseReportByAccountDto> reportList = new ArrayList<>();
        ResponseReportByAccountDto report1 = new ResponseReportByAccountDto(LocalDate.of(2023,5,2),
                "Test client",
                123456789L,
                "AHORROS",
                5000.0,
                true,
                1000.0,
                4000.0);
        reportList.add(report1);
        ResponseReportByAccountDto report2 = new ResponseReportByAccountDto(LocalDate.of(2023,5,3),
                "Test client2",
                123456788L,
                "CORRIENTE",
                500.0,
                true,
                100.0,
                400.0);
        reportList.add(report2);

        when(transactionService.generateReportStatusAccount(initialDate, endDate, clientId))
                .thenReturn(reportList);

        mockMvc.perform(get("/movimientos/reportes")
                        .param("fechaDesde", "01/05/2023")
                        .param("fechaHasta", "31/05/2023")
                        .param("cliente", "123456788"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].accountNumber").value(report1.getAccountNumber()))
                .andExpect(jsonPath("$[1].accountNumber").value(report2.getAccountNumber()));

        verify(transactionService, times(1)).generateReportStatusAccount(initialDate, endDate, clientId);
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
