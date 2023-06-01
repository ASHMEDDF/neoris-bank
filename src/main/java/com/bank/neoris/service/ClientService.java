package com.bank.neoris.service;

import com.bank.neoris.domain.Account;
import com.bank.neoris.domain.Client;
import com.bank.neoris.exception.account.AccountIsNotZeroException;
import com.bank.neoris.exception.user.UserAlreadyExistsException;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.exception.user.UserNotLegalAgeException;
import com.bank.neoris.repository.AccountRepository;
import com.bank.neoris.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ClientService {

    private ClientRepository clientRepository;
    private AccountRepository accountRepository;

    public Client saveNewClient (Client clientToCreate) {

        if (clientRepository.findById(clientToCreate.getIdentification()).isPresent()) {
            throw new UserAlreadyExistsException(clientToCreate.getIdentification());
        }
        ageValidate(clientToCreate);

        Client newClient = new Client();
        newClient.setIdentification(clientToCreate.getIdentification());
        newClient.setName(clientToCreate.getName());
        newClient.setGender(clientToCreate.getGender());
        newClient.setAge(clientToCreate.getAge());
        newClient.setAddress(clientToCreate.getAddress());
        newClient.setPhone(clientToCreate.getPhone());
        newClient.setPassword(clientToCreate.getPassword());
        newClient.setState(clientToCreate.getState());

        return clientRepository.save(newClient);
    }



    public Client updateClient (Client clientToUpdate) {

        ageValidate(clientToUpdate);

        if (clientRepository.existsById(clientToUpdate.getIdentification())){
            Client updatedClient = new Client();
            updatedClient.setIdentification(clientToUpdate.getIdentification());
            updatedClient.setName(clientToUpdate.getName());
            updatedClient.setGender(clientToUpdate.getGender());
            updatedClient.setAge(clientToUpdate.getAge());
            updatedClient.setAddress(clientToUpdate.getAddress());
            updatedClient.setPhone(clientToUpdate.getPhone());
            updatedClient.setPassword(clientToUpdate.getPassword());
            updatedClient.setState(clientToUpdate.getState());

            return clientRepository.save(updatedClient);

        }
        throw new UserNotFoundException(clientToUpdate.getIdentification());

    }

    private void ageValidate(Client clientToCreate) {
        if (clientToCreate.getAge() < 18) {
            throw new UserNotLegalAgeException();
        }
    }

    public Optional<Client> getByClientId (Long clientId) {
        return Optional.ofNullable(clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException(clientId)));
    }

    public Boolean deleteByClientId (Long clientId) {

        Optional<Client> client = clientRepository.findById(clientId);

        if (client.isPresent()){
            boolean allAccountsHaveZeroBalance = accountRepository.findAllByClientIdentification(clientId)
                    .stream()
                    .allMatch(account -> account.getInitialBalance() == 0);

            if (allAccountsHaveZeroBalance){
                clientRepository.delete(client.get());
                return true;
            } else {
                List<Account> accountsWithNonZeroBalance = accountRepository.findAllByClientIdentification(clientId)
                        .stream()
                        .filter(account -> account.getInitialBalance() != 0)
                        .toList();
                throw new AccountIsNotZeroException(clientId, accountsWithNonZeroBalance.stream().map(Account::getAccountNumber).toList());
            }

        }
        throw new UserNotFoundException(clientId);
    }

}
