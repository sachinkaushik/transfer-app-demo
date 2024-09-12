package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.BadRequestException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@Slf4j
public class TransferService {

    @Getter
    private final AccountsRepository accountsRepository;

    @Autowired
    public TransferService(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    public TransferResponse transferMoney(TransferDTO transferDTO) throws BadRequestException {
        validateRequest(transferDTO);
        return this.accountsRepository.transferMoney(transferDTO);
    }

    private void validateRequest(TransferDTO transferDTO) {
        log.info("Validating transfer request....");
        if (transferDTO.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Transfer amount must be positive");
        }
        Account accountFrom = accountsRepository.getAccount(transferDTO.getAccountFromId());
        if (Objects.isNull(accountFrom)) {
            throw new BadRequestException("AccountFromId doesn't exist");
        }
        if (Objects.isNull(accountsRepository.getAccount(transferDTO.getAccountToId()))) {
            throw new BadRequestException("AccountToId doesn't exist");
        }
        if (transferDTO.getBalance().compareTo(accountFrom.getBalance()) > 0) {
            throw new BadRequestException("Insufficient balance");
        }
        log.info("Transfer request validated successfully....");
    }
}
