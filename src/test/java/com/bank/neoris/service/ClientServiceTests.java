package com.bank.neoris.service;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.exception.account.AccountIsNotZeroException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTests {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNewClient_Success() {
        Client clientToCreate = new Client();
        clientToCreate.setIdentification(123456788L);
        clientToCreate.setName("Test Name");
        clientToCreate.setAge(25);

        when(clientRepository.findById(clientToCreate.getIdentification())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).thenReturn(clientToCreate);

        Client savedClient = clientService.saveNewClient(clientToCreate);

        assertNotNull(savedClient);
        assertEquals(clientToCreate.getIdentification(), savedClient.getIdentification());
        assertEquals(clientToCreate.getName(), savedClient.getName());
        assertEquals(clientToCreate.getAge(), savedClient.getAge());

        verify(clientRepository, times(1)).findById(clientToCreate.getIdentification());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void testSaveNewClient_AlreadyExists() {
        Client clientToCreate = new Client();
        clientToCreate.setIdentification(123456788L);

        when(clientRepository.findById(clientToCreate.getIdentification())).thenReturn(Optional.of(clientToCreate));

        assertThrows(UserAlreadyExistsException.class, () -> clientService.saveNewClient(clientToCreate));

        verify(clientRepository, times(1)).findById(clientToCreate.getIdentification());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testSaveNewClient_NotLegalAge() {
        Client clientToCreate = new Client();
        clientToCreate.setIdentification(123456788L);
        clientToCreate.setAge(16);

        assertThrows(UserNotLegalAgeException.class, () -> clientService.saveNewClient(clientToCreate));

        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testUpdateClient_Success() {
        // Arrange
        Client clientToUpdate = new Client();
        clientToUpdate.setIdentification(123456788L);
        clientToUpdate.setName("Test Name");
        clientToUpdate.setAge(25);

        ClientRepository clientRepositoryMock = mock(ClientRepository.class);
        when(clientRepositoryMock.existsById(clientToUpdate.getIdentification())).thenReturn(true);
        when(clientRepositoryMock.save(any(Client.class))).thenReturn(clientToUpdate);

        // Crear una instancia de BankService y establecer el mock del ClientRepository
        ClientService clientService = new ClientService(clientRepositoryMock, accountRepository);

        Client updatedClient = clientService.updateClient(clientToUpdate);

        assertNotNull(updatedClient);
        assertEquals(clientToUpdate.getIdentification(), updatedClient.getIdentification());
        assertEquals(clientToUpdate.getName(), updatedClient.getName());
        assertEquals(clientToUpdate.getAge(), updatedClient.getAge());

        verify(clientRepositoryMock, times(1)).existsById(clientToUpdate.getIdentification());
        verify(clientRepositoryMock, times(1)).save(any(Client.class));
    }

    @Test
    void testUpdateClient_NotLegalAge() {
        Client clientToUpdate = new Client();
        clientToUpdate.setIdentification(123456788L);
        clientToUpdate.setAge(16);

        assertThrows(UserNotLegalAgeException.class, () -> clientService.updateClient(clientToUpdate));

        verify(clientRepository, never()).existsById(anyLong());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testUpdateClient_NotFound() {
        Client clientToUpdate = new Client();
        clientToUpdate.setIdentification(123456788L);
        clientToUpdate.setAge(25);

        when(clientRepository.existsById(clientToUpdate.getIdentification())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> clientService.updateClient(clientToUpdate));

        verify(clientRepository, times(1)).existsById(clientToUpdate.getIdentification());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    void testGetByClientId_Success() {
        Long clientId = 123456788L;
        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("Test Name");
        client.setAge(25);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Optional<Client> retrievedClient = clientService.getByClientId(clientId);

        assertTrue(retrievedClient.isPresent());
        assertEquals(client.getIdentification(), retrievedClient.get().getIdentification());
        assertEquals(client.getName(), retrievedClient.get().getName());
        assertEquals(client.getAge(), retrievedClient.get().getAge());

        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void testGetByClientId_NotFound() {
        Long clientId = 123456788L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> clientService.getByClientId(clientId));

        verify(clientRepository, times(1)).findById(clientId);
    }

    @Test
    void testDeleteByClientId_Success() {
        Long clientId = 123456788L;
        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("Test Name");
        client.setAge(25);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClientIdentification(clientId)).thenReturn(Collections.emptyList());

        Boolean deleted = clientService.deleteByClientId(clientId);
        assertTrue(deleted);

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(1)).findAllByClientIdentification(clientId);
        verify(clientRepository, times(1)).delete(client);
    }

    @Test
    void testDeleteByClientId_NotFound() {
        Long clientId = 123456788L;

        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> clientService.deleteByClientId(clientId));

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, never()).findAllByClientIdentification(anyLong());
        verify(clientRepository, never()).delete(any(Client.class));
    }

    @Test
    void testDeleteByClientId_AccountNotZero() {
        Long clientId = 123456788L;
        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("Test Name");
        client.setAge(25);

        // dos cuentas con saldo distinto de cero
        Account account1 = new Account();
        account1.setAccountNumber(1L);
        account1.setInitialBalance(100);
        Account account2 = new Account();
        account2.setAccountNumber(2L);
        account2.setInitialBalance(200);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(accountRepository.findAllByClientIdentification(clientId)).thenReturn(Arrays.asList(account1, account2));

        AccountIsNotZeroException exception = assertThrows(AccountIsNotZeroException.class, () -> clientService.deleteByClientId(clientId));

        assertEquals("Client with ID 123456788 has money in the account(s) 1, 2", exception.getMessage());

        verify(clientRepository, times(1)).findById(clientId);
        verify(accountRepository, times(2)).findAllByClientIdentification(clientId);
        verify(clientRepository, never()).delete(any(Client.class));
    }

}
