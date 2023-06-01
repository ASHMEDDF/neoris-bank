package com.bank.neoris.controller;

import com.bank.neoris.domain.Transaction;
import com.bank.neoris.dto.CreateTransactionRequestDto;
import com.bank.neoris.dto.ResponseReportByAccountDto;
import com.bank.neoris.dto.ResponseTransactionSavedDto;
import com.bank.neoris.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class TransactionController {

    private TransactionService transactionService;

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
    public ResponseEntity<Page<ResponseReportByAccountDto>> generateReportStatusAccount(
            @RequestParam("fechaDesde") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate initialDate,
            @RequestParam("fechaHasta") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,
            @RequestParam("cliente") Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ResponseReportByAccountDto> reportPage = transactionService.generateReportStatusAccount(initialDate, endDate, clientId, pageable);

        return ResponseEntity.ok(reportPage);
    }

}
