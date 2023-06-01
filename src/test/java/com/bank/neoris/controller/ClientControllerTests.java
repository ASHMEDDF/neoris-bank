package com.bank.neoris.controller;

import com.bank.neoris.domain.Client;
import com.bank.neoris.exception.user.UserNotFoundException;
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

import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ClientControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private TransactionService transactionService;

    @Test
    void testCreateClient_Success() throws Exception {
        Client createClientRequest = new Client();
        createClientRequest.setName("Test Name");
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
        when(clientService.saveNewClient(any(Client.class))).thenReturn(savedClient);

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(savedClient.getName()))
                .andExpect(jsonPath("$.address").value(savedClient.getAddress()))
                .andExpect(jsonPath("$.phone").value(savedClient.getPhone()))
                .andExpect(jsonPath("$.password").value(savedClient.getPassword()))
                .andExpect(jsonPath("$.state").value(savedClient.getState()));

        verify(clientService, times(1)).saveNewClient(any(Client.class));
    }

    @Test
    void testGetClientById_Success() throws Exception {
        Long clientId = 12345L;

        Client client = new Client();
        client.setIdentification(clientId);
        client.setName("Test Name");
        client.setGender("Male");
        client.setAge(30);
        client.setAddress("123 Main St");
        client.setPhone(123456L);
        client.setPassword(1245);
        client.setState(true);

        when(clientService.getByClientId(clientId)).thenReturn(Optional.of(client));

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

        verify(clientService, times(1)).getByClientId(clientId);
    }

    @Test
    void testDeleteClientById_ClientFound() throws Exception {
        Long clientId = 12345L;

        when(clientService.deleteByClientId(clientId)).thenReturn(true);

        mockMvc.perform(delete("/clientes/{clientId}", clientId))
                .andExpect(status().isAccepted());

        verify(clientService, times(1)).deleteByClientId(clientId);
    }

    @Test
    void testDeleteClientById_ClientNotFound() throws Exception {
        Long clientId = 12345L;

        when(clientService.deleteByClientId(clientId)).thenReturn(false);

        mockMvc.perform(delete("/clientes/{clientId}", clientId))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).deleteByClientId(clientId);
    }

    @Test
    void testUpdateClient_ClientFound() throws Exception {
        Client createClientRequest = new Client();
        createClientRequest.setName("Test Name");
        createClientRequest.setAddress("123 Main St");
        createClientRequest.setPhone(123456L);
        createClientRequest.setPassword(1245);
        createClientRequest.setState(true);

        Client updatedClient = new Client();
        updatedClient.setName("Test Name");
        updatedClient.setAddress("456 Elm St");
        updatedClient.setPhone(654321L);
        updatedClient.setPassword(9876);
        updatedClient.setState(false);

        when(clientService.updateClient(any(Client.class))).thenReturn(updatedClient);

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedClient.getName()))
                .andExpect(jsonPath("$.address").value(updatedClient.getAddress()))
                .andExpect(jsonPath("$.phone").value(updatedClient.getPhone()))
                .andExpect(jsonPath("$.password").value(updatedClient.getPassword()))
                .andExpect(jsonPath("$.state").value(updatedClient.getState()));

        verify(clientService, times(1)).updateClient(any(Client.class));
    }

    @Test
    void testUpdateClient_ClientNotFound() throws Exception {
        Client createClientRequest = new Client();
        createClientRequest.setIdentification(1L);
        createClientRequest.setName("Test Name");
        createClientRequest.setAddress("123 Main St");
        createClientRequest.setPhone(123456L);
        createClientRequest.setPassword(1245);
        createClientRequest.setState(true);

        when(clientService.updateClient(any(Client.class))).thenThrow(new UserNotFoundException(createClientRequest.getIdentification()));

        mockMvc.perform(put("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createClientRequest)))
                .andExpect(status().isNotFound());

        verify(clientService, times(1)).updateClient(any(Client.class));
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
