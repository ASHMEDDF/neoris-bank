package com.bank.neoris.service;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.domain.Transaction;
import com.bank.neoris.dto.CreateTransactionRequestDto;
import com.bank.neoris.dto.ResponseReportByAccountDto;
import com.bank.neoris.enumerations.TransactionTypes;
import com.bank.neoris.exception.account.AccountNotExistsException;
import com.bank.neoris.exception.account.AccountsNotFoundException;
import com.bank.neoris.exception.transaction.TransactionNotFoundException;
import com.bank.neoris.exception.transaction.TransactionValuesException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.repository.AccountRepository;
import com.bank.neoris.repository.ClientRepository;
import com.bank.neoris.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TransactionService {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private ClientRepository clientRepository;

    public Transaction saveTransaction (CreateTransactionRequestDto transactionToCreate) {

        Optional<Account> account = accountRepository.findById(transactionToCreate.getAccountNumber());

        if (account.isPresent()) {
            Transaction newTransaction = new Transaction();

            newTransaction.setTransactionType(transactionToCreate.getTransactionType().name());

            double balanceAccount = operationValue(transactionToCreate, account.get());
            newTransaction.setInitialBalance(account.get().getInitialBalance());
            newTransaction.setTransactionValue(transactionToCreate.getTransactionValue());
            newTransaction.setFinalBalance(balanceAccount);
            newTransaction.setDate(setDate());
            newTransaction.setAccountNumber(transactionToCreate.getAccountNumber());

            account.get().setInitialBalance(balanceAccount);
            accountRepository.save(account.get());

            return transactionRepository.save(newTransaction);
        }
        throw new AccountNotExistsException(transactionToCreate.getAccountNumber());

    }

    private double operationValue(CreateTransactionRequestDto transactionToCreate, Account account) {
        if (transactionToCreate.getTransactionType() == TransactionTypes.CREDITO){
            return transactionToCreate.getTransactionValue() + account.getInitialBalance();
        }else if (account.getInitialBalance() > 0 && account.getInitialBalance() >= transactionToCreate.getTransactionValue()){
            return account.getInitialBalance() - transactionToCreate.getTransactionValue() ;
        }
        throw new TransactionValuesException();
    }

    private LocalDate setDate() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);

        return LocalDate.parse(formattedDate, formatter);
    }

    public Page<ResponseReportByAccountDto> generateReportStatusAccount(LocalDate initialDate, LocalDate endDate, Long clientId, Pageable pageable) {
        Page<Transaction> transactionsPage = transactionRepository.findByDateBetween(initialDate, endDate, pageable);

        if (transactionsPage.isEmpty()) {
            throw new TransactionNotFoundException();
        }

        Optional<Client> client = clientRepository.findById(clientId);

        if (client.isEmpty()) {
            throw new UserNotFoundException(clientId);
        }

        List<Account> accountList = accountRepository.findAllByClientIdentification(clientId);

        if (accountList.isEmpty()) {
            throw new AccountsNotFoundException(clientId);
        }

        return transactionsPage.map(transaction -> {
            Account account = accountList.stream()
                    .filter(acc -> acc.getAccountNumber().equals(transaction.getAccountNumber()))
                    .findFirst()
                    .orElse(null);

            assert account != null;

            return new ResponseReportByAccountDto(transaction.getDate(),
                    client.get().getName(),
                    transaction.getAccountNumber(),
                    account.getAccountType().toString(),
                    transaction.getInitialBalance(),
                    account.getState(),
                    transaction.getTransactionValue(),
                    transaction.getFinalBalance());
        });
    }
}
