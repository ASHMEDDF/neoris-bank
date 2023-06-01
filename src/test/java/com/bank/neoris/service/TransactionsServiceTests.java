package com.bank.neoris.service;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.domain.Transaction;
import com.bank.neoris.dto.CreateTransactionRequestDto;
import com.bank.neoris.dto.ResponseReportByAccountDto;
import com.bank.neoris.enumerations.AccountTypes;
import com.bank.neoris.enumerations.TransactionTypes;
import com.bank.neoris.exception.account.AccountNotExistsException;
import com.bank.neoris.exception.account.AccountsNotFoundException;
import com.bank.neoris.exception.transaction.TransactionNotFoundException;
import com.bank.neoris.exception.transaction.TransactionValuesException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.repository.AccountRepository;
import com.bank.neoris.repository.ClientRepository;
import com.bank.neoris.repository.TransactionRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionsServiceTests {


    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTransaction_Success_CreditTransaction() {
        Long accountNumber = 123456789L;
        double initialBalance = 1000.0;
        double transactionValue = 500.0;
        TransactionTypes transactionType = TransactionTypes.CREDITO;

        CreateTransactionRequestDto transactionToCreate = new CreateTransactionRequestDto();
        transactionToCreate.setAccountNumber(accountNumber);
        transactionToCreate.setTransactionValue(transactionValue);
        transactionToCreate.setTransactionType(transactionType);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setInitialBalance(initialBalance);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.saveTransaction(transactionToCreate);

        assertNotNull(savedTransaction);
        assertEquals(transactionToCreate.getTransactionType().name(), savedTransaction.getTransactionType());
        assertEquals(initialBalance, savedTransaction.getInitialBalance(), 0.0);
        assertEquals(transactionValue, savedTransaction.getTransactionValue(), 0.0);
        assertEquals(initialBalance + transactionValue, savedTransaction.getFinalBalance(), 0.0);
        assertNotNull(savedTransaction.getDate());
        assertEquals(accountNumber, savedTransaction.getAccountNumber());

        verify(accountRepository, times(1)).findById(accountNumber);
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_Success_DebitTransaction() {
        Long accountNumber = 123456789L;
        double initialBalance = 1000.0;
        double transactionValue = 500.0;
        TransactionTypes transactionType = TransactionTypes.DEBITO;

        CreateTransactionRequestDto transactionToCreate = new CreateTransactionRequestDto();
        transactionToCreate.setAccountNumber(accountNumber);
        transactionToCreate.setTransactionValue(transactionValue);
        transactionToCreate.setTransactionType(transactionType);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setInitialBalance(initialBalance);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction savedTransaction = transactionService.saveTransaction(transactionToCreate);

        assertNotNull(savedTransaction);
        assertEquals(transactionToCreate.getTransactionType().name(), savedTransaction.getTransactionType());
        assertEquals(initialBalance, savedTransaction.getInitialBalance(), 0.0);
        assertEquals(transactionValue, savedTransaction.getTransactionValue(), 0.0);
        assertEquals(initialBalance - transactionValue, savedTransaction.getFinalBalance(), 0.0);
        assertNotNull(savedTransaction.getDate());
        assertEquals(accountNumber, savedTransaction.getAccountNumber());

        verify(accountRepository, times(1)).findById(accountNumber);
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_AccountNotExists() {
        Long accountNumber = 123456789L;
        double transactionValue = 500.0;
        TransactionTypes transactionType = TransactionTypes.CREDITO;

        CreateTransactionRequestDto transactionToCreate = new CreateTransactionRequestDto();
        transactionToCreate.setAccountNumber(accountNumber);
        transactionToCreate.setTransactionValue(transactionValue);
        transactionToCreate.setTransactionType(transactionType);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.empty());

        assertThrows(AccountNotExistsException.class, () -> transactionService.saveTransaction(transactionToCreate));

        verify(accountRepository, times(1)).findById(accountNumber);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testSaveTransaction_InsufficientBalance() {
        Long accountNumber = 123456789L;
        double initialBalance = 100.0;
        double transactionValue = 500.0;
        TransactionTypes transactionType = TransactionTypes.DEBITO;

        CreateTransactionRequestDto transactionToCreate = new CreateTransactionRequestDto();
        transactionToCreate.setAccountNumber(accountNumber);
        transactionToCreate.setTransactionValue(transactionValue);
        transactionToCreate.setTransactionType(transactionType);

        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setInitialBalance(initialBalance);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(account));

        assertThrows(TransactionValuesException.class, () -> transactionService.saveTransaction(transactionToCreate));

        verify(accountRepository, times(1)).findById(accountNumber);
        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testGenerateReportStatusAccount_Success() {
        LocalDate initialDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);
        Long clientId = 123456788L;

        Transaction transaction1 = new Transaction();
        transaction1.setAccountNumber(123456789L);
        transaction1.setInitialBalance(1000.0);
        transaction1.setTransactionValue(500.0);
        transaction1.setFinalBalance(1500.0);
        transaction1.setDate(LocalDate.of(2022, 1, 10));

        Transaction transaction2 = new Transaction();
        transaction2.setAccountNumber(987654321L);
        transaction2.setInitialBalance(2000.0);
        transaction2.setTransactionValue(1000.0);
        transaction2.setFinalBalance(3000.0);
        transaction2.setDate(LocalDate.of(2022, 2, 15));

        List<Transaction> transactionsList = Arrays.asList(transaction1, transaction2);

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("Test Name");

        Account account1 = new Account();
        account1.setAccountNumber(123456789L);
        account1.setAccountType(AccountTypes.CORRIENTE);
        account1.setState(true);

        Account account2 = new Account();
        account2.setAccountNumber(987654321L);
        account2.setAccountType(AccountTypes.AHORROS);
        account2.setState(true);

        List<Account> accountList = Arrays.asList(account1, account2);

        Page<Transaction> transactionsPage = new PageImpl<>(transactionsList);

        when(transactionRepository.findByDateBetween(initialDate, endDate, PageRequest.of(0, 10))).thenReturn(transactionsPage);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClientIdentification(clientId)).thenReturn(accountList);

        Page<ResponseReportByAccountDto> reportPage = transactionService.generateReportStatusAccount(initialDate, endDate, clientId, PageRequest.of(0, 10));

        assertNotNull(reportPage);
        List<ResponseReportByAccountDto> report = reportPage.getContent();
        assertEquals(2, report.size());

        ResponseReportByAccountDto report1 = report.get(0);
        assertEquals(transaction1.getDate(), report1.getDate());
        assertEquals(client.getName(), report1.getClient());
        assertEquals(transaction1.getAccountNumber(), report1.getAccountNumber());
        assertEquals(account1.getAccountType().toString(), report1.getType());
        assertEquals(transaction1.getInitialBalance(), report1.getInitialBalance(), 0.0);
        assertEquals(transaction1.getTransactionValue(), report1.getTransaction(), 0.0);
        assertEquals(transaction1.getFinalBalance(), report1.getAvailableBalance(), 0.0);

        ResponseReportByAccountDto report2 = report.get(1);
        assertEquals(transaction2.getDate(), report2.getDate());
        assertEquals(client.getName(), report2.getClient());
        assertEquals(transaction2.getAccountNumber(), report2.getAccountNumber());
        assertEquals(account2.getAccountType().toString(), report2.getType());
        assertEquals(transaction2.getInitialBalance(), report2.getInitialBalance(), 0.0);
        assertEquals(transaction2.getTransactionValue(), report2.getTransaction(), 0.0);
        assertEquals(transaction2.getFinalBalance(), report2.getAvailableBalance(), 0.0);

        verify(transactionRepository, times(1)).findByDateBetween(initialDate, endDate, PageRequest.of(0, 10));
        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).findAllByClientIdentification(clientId);
    }

    @Test
    void testGenerateReportStatusAccount_TransactionNotFound() {
        LocalDate initialDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);
        Long clientId = 123456788L;

        Page<Transaction> emptyTransactionPage = new PageImpl<>(Collections.emptyList());

        when(transactionRepository.findByDateBetween(initialDate, endDate, PageRequest.of(0, 10))).thenReturn(emptyTransactionPage);

        assertThrows(TransactionNotFoundException.class, () -> transactionService.generateReportStatusAccount(initialDate, endDate, clientId, PageRequest.of(0, 10)));

        verify(transactionRepository, times(1)).findByDateBetween(initialDate, endDate, PageRequest.of(0, 10));
        verify(clientRepository, never()).findById(any(Long.class));
        verify(accountRepository, never()).findAllByClientIdentification(any(Long.class));
    }

    @Test
    void testGenerateReportStatusAccount_ClientNotFound() {
        LocalDate initialDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);
        Long clientId = 123456788L;

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(123456789L);
        transaction.setInitialBalance(1000.0);
        transaction.setTransactionValue(500.0);
        transaction.setFinalBalance(1500.0);
        transaction.setDate(LocalDate.of(2022, 1, 10));

        List<Transaction> transactionsList = Collections.singletonList(transaction);

        Page<Transaction> transactionsPage = new PageImpl<>(transactionsList);

        when(transactionRepository.findByDateBetween(initialDate, endDate, PageRequest.of(0, 10))).thenReturn(transactionsPage);
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.generateReportStatusAccount(initialDate, endDate, clientId, PageRequest.of(0, 10)));

        verify(transactionRepository, times(1)).findByDateBetween(initialDate, endDate, PageRequest.of(0, 10));
        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, never()).findAllByClientIdentification(any(Long.class));
    }


    @Test
    void testGenerateReportStatusAccount_AccountsNotFound() {
        LocalDate initialDate = LocalDate.of(2022, 1, 1);
        LocalDate endDate = LocalDate.of(2022, 12, 31);
        Long clientId = 123456788L;

        Transaction transaction = new Transaction();
        transaction.setAccountNumber(123456789L);
        transaction.setInitialBalance(1000.0);
        transaction.setTransactionValue(500.0);
        transaction.setFinalBalance(1500.0);
        transaction.setDate(LocalDate.of(2022, 1, 10));

        List<Transaction> transactionsList = Collections.singletonList(transaction);

        Page<Transaction> transactionsPage = new PageImpl<>(transactionsList);

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("Test Name");

        when(transactionRepository.findByDateBetween(initialDate, endDate, PageRequest.of(0, 10))).thenReturn(transactionsPage);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClientIdentification(clientId)).thenReturn(Collections.emptyList());

        assertThrows(AccountsNotFoundException.class, () -> transactionService.generateReportStatusAccount(initialDate, endDate, clientId, PageRequest.of(0, 10)));

        verify(transactionRepository, times(1)).findByDateBetween(initialDate, endDate, PageRequest.of(0, 10));
        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).findAllByClientIdentification(clientId);
    }

}
