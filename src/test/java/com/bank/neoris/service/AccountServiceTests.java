package com.bank.neoris.service;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.enumerations.AccountTypes;
import com.bank.neoris.exception.account.AccountIsNotZeroException;
import com.bank.neoris.exception.account.AccountsNotFoundException;
import com.bank.neoris.exception.user.UserAlreadyExistsException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.exception.user.UserNotLegalAgeException;
import com.bank.neoris.repository.AccountRepository;
import com.bank.neoris.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAccount_Success() {
        Long clientId = 123456788L;
        Account accountToCreate = new Account();
        accountToCreate.setAccountType(AccountTypes.AHORROS);
        accountToCreate.setInitialBalance(1000.0);
        accountToCreate.setState(true);
        accountToCreate.setClientIdentification(clientId);

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("John Doe");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.save(any(Account.class))).thenReturn(accountToCreate);

        Account savedAccount = accountService.saveAccount(accountToCreate);

        assertNotNull(savedAccount);
        assertEquals(accountToCreate.getAccountType(), savedAccount.getAccountType());
        assertEquals(accountToCreate.getInitialBalance(), savedAccount.getInitialBalance());
        assertEquals(accountToCreate.getState(), savedAccount.getState());
        assertEquals(accountToCreate.getClientIdentification(), savedAccount.getClientIdentification());

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testSaveAccount_UserNotFound() {
        Long clientId = 123456788L;
        Account accountToCreate = new Account();
        accountToCreate.setAccountType(AccountTypes.AHORROS);
        accountToCreate.setInitialBalance(1000.0);
        accountToCreate.setState(true);
        accountToCreate.setClientIdentification(clientId);

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> accountService.saveAccount(accountToCreate));

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void testGetAccountsByClientId_Success() {
        Long clientId = 123456788L;

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("John Doe");

        Account account1 = new Account();
        account1.setAccountType(AccountTypes.AHORROS);
        account1.setInitialBalance(1000.0);
        account1.setState(true);
        account1.setClientIdentification(clientId);

        Account account2 = new Account();
        account2.setAccountType(AccountTypes.AHORROS);
        account2.setInitialBalance(500.0);
        account2.setState(true);
        account2.setClientIdentification(clientId);

        List<Account> accounts = Arrays.asList(account1, account2);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClientIdentification(clientId)).thenReturn(accounts);

        Optional<List<Account>> retrievedAccounts = accountService.getAccountsByClientId(clientId);

        assertTrue(retrievedAccounts.isPresent());
        assertEquals(accounts.size(), retrievedAccounts.get().size());

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).findAllByClientIdentification(clientId);
    }

    @Test
    void testGetAccountsByClientId_UserNotFound() {
        Long clientId = 123456788L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> accountService.getAccountsByClientId(clientId));

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, never()).findAllByClientIdentification(clientId);
    }

    @Test
    void testGetAccountsByClientId_AccountsNotFound() {
        Long clientId = 123456788L;

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("John Doe");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClientIdentification(clientId)).thenReturn(Collections.emptyList());

        assertThrows(AccountsNotFoundException.class, () -> accountService.getAccountsByClientId(clientId));

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).findAllByClientIdentification(clientId);
    }
}
