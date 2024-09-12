package com.dws.challenge.service;

import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.BadRequestException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferService {

    @Getter
    private final AccountsRepository accountsRepository;

    @Autowired
    public TransferService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public TransferResponse transferMoney(TransferDTO transferDTO) throws BadRequestException {
        return this.accountsRepository.transferMoney(transferDTO);
    }
}
