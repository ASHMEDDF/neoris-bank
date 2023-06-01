package com.bank.neoris.controller;

import com.bank.neoris.domain.Client;
import com.bank.neoris.dto.ResponseClientByIdDto;
import com.bank.neoris.dto.ResponseClientSavedDto;
import com.bank.neoris.exception.user.UserNotFoundException;
import com.bank.neoris.service.ClientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class ClientController {

    private ClientService clientService;

    @PostMapping("clientes")
    public ResponseEntity<ResponseClientSavedDto> createClient (@RequestBody @Valid Client createClientRequest) {

        Client saveNewClient = clientService.saveNewClient(createClientRequest);
        return new ResponseEntity<>(new ResponseClientSavedDto(saveNewClient.getName(),
                saveNewClient.getAddress(),
                saveNewClient.getPhone(),
                saveNewClient.getPassword(),
                saveNewClient.getState()),
                HttpStatus.OK);
    }

    @GetMapping("clientes/{clientId}")
    public ResponseEntity<ResponseClientByIdDto> getClientById(@PathVariable Long clientId) {
        Client clientOptional = clientService.getByClientId(clientId)
                .orElseThrow(() -> new UserNotFoundException(clientId));

        ResponseClientByIdDto loanResponseDto = new ResponseClientByIdDto(clientOptional.getIdentification(),
                clientOptional.getName(),
                clientOptional.getGender(),
                clientOptional.getAge(),
                clientOptional.getAddress(),
                clientOptional.getPhone(),
                clientOptional.getPassword(),
                clientOptional.getState());
        return ResponseEntity.ok(loanResponseDto);

    }

    @DeleteMapping("clientes/{clientId}")
    public ResponseEntity<String> deleteClientById(@PathVariable Long clientId) {

        boolean deleted = clientService.deleteByClientId(clientId);

        if (deleted) {
            return ResponseEntity.accepted().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PutMapping("clientes")
    public ResponseEntity<ResponseClientSavedDto> updateClient (@RequestBody @Valid Client createClientRequest) {

        Client saveNewClient = clientService.updateClient(createClientRequest);
        return new ResponseEntity<>(new ResponseClientSavedDto(saveNewClient.getName(),
                saveNewClient.getAddress(),
                saveNewClient.getPhone(),
                saveNewClient.getPassword(),
                saveNewClient.getState()),
                HttpStatus.OK);
    }
}
