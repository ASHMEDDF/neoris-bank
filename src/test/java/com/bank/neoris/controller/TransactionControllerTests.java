package com.bank.neoris.controller;

import com.bank.neoris.domain.Transaction;
import com.bank.neoris.dto.CreateTransactionRequestDto;
import com.bank.neoris.dto.ResponseReportByAccountDto;
import com.bank.neoris.enumerations.TransactionTypes;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
class TransactionControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;
    @MockBean
    private AccountService accountService;
    @MockBean
    private TransactionService transactionService;

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
        ResponseReportByAccountDto report1 = new ResponseReportByAccountDto(LocalDate.of(2023, 5, 2),
                "Test client",
                123456789L,
                "AHORROS",
                5000.0,
                true,
                1000.0,
                4000.0);
        reportList.add(report1);
        ResponseReportByAccountDto report2 = new ResponseReportByAccountDto(LocalDate.of(2023, 5, 3),
                "Test client2",
                123456788L,
                "CORRIENTE",
                500.0,
                true,
                100.0,
                400.0);
        reportList.add(report2);

        Page<ResponseReportByAccountDto> reportPage = new PageImpl<>(reportList);

        when(transactionService.generateReportStatusAccount(initialDate, endDate, clientId, PageRequest.of(0, 10)))
                .thenReturn(reportPage);

        mockMvc.perform(get("/movimientos/reportes")
                        .param("fechaDesde", "01/05/2023")
                        .param("fechaHasta", "31/05/2023")
                        .param("cliente", "123456788"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].accountNumber").value(report1.getAccountNumber()))
                .andExpect(jsonPath("$.content[1].accountNumber").value(report2.getAccountNumber()));

        verify(transactionService, times(1)).generateReportStatusAccount(initialDate, endDate, clientId, PageRequest.of(0, 10));
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
