package com.bank.neoris.service;

import com.bank.neoris.domain.Account;
import com.bank.neoris.exception.account.AccountsNotFoundException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.repository.AccountRepository;
import com.bank.neoris.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AccountService {

    private AccountRepository accountRepository;
    private ClientRepository clientRepository;


    public Account saveAccount (Account accountToCreate) {

        if (clientRepository.findById(accountToCreate.getClientIdentification()).isEmpty()) {
            throw new UserNotFoundException(accountToCreate.getClientIdentification());
        }

        Account newAccount = new Account();
        newAccount.setAccountType(accountToCreate.getAccountType());
        newAccount.setInitialBalance(accountToCreate.getInitialBalance());
        newAccount.setState(accountToCreate.getState());
        newAccount.setClientIdentification(accountToCreate.getClientIdentification());

        return accountRepository.save(newAccount);
    }

    public List<Account> getAccountsByClientId (Long clientId) {

        if (clientRepository.findById(clientId).isEmpty()) {
            throw new UserNotFoundException(clientId);
        }
        List<Account> allByClientIdentification = accountRepository.findAllByClientIdentification(clientId);

        if (allByClientIdentification.isEmpty()){
            throw new AccountsNotFoundException(clientId);
        }
        return allByClientIdentification;

    }

}
