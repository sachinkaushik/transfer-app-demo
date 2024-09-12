package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.BadRequestException;
import com.dws.challenge.exception.DuplicateAccountIdException;


public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  TransferResponse transferMoney(TransferDTO transferDTO) throws BadRequestException;
}
