package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.BadRequestException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.TransferFailedException;
import com.dws.challenge.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final Map<String, Lock> accountLocks = new ConcurrentHashMap<>(); // Lock for each account

    @Autowired
    private NotificationService notificationService;

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        accountLocks.putIfAbsent(account.getAccountId(), new ReentrantLock());  // Initialize lock for new account
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
        accountLocks.clear();
    }

    public void debit(Account accountFrom, BigDecimal amount) {
        BigDecimal subtract = accountFrom.getBalance().subtract(amount);
        accountFrom.setBalance(subtract);
        accounts.put(accountFrom.getAccountId(), accountFrom);
    }

    public void credit(Account accountTo, BigDecimal amount) {
        BigDecimal add = accountTo.getBalance().add(amount);
        accountTo.setBalance(add);
        accounts.put(accountTo.getAccountId(), accountTo);
    }

    @Override
    //@Transactional
    public TransferResponse transferMoney(TransferDTO transferDTO) throws BadRequestException {
        Account accountFrom = this.getAccount(transferDTO.getAccountFromId());
        Account accountTo = this.getAccount(transferDTO.getAccountToId());
        Lock lock1, lock2;
        if (accountFrom.getAccountId().compareTo(accountTo.getAccountId()) < 0) {
            lock1 = accountLocks.get(accountFrom.getAccountId());
            lock2 = accountLocks.get(accountTo.getAccountId());
        } else {
            lock1 = accountLocks.get(accountTo.getAccountId());
            lock2 = accountLocks.get(accountFrom.getAccountId());
        }
        TransferResponse response = new TransferResponse();
        response.setAccountId(transferDTO.getAccountToId());
        // Lock both accounts to ensure thread safety
        lock1.lock();
        lock2.lock();
        try {
            if (transferDTO.getBalance().compareTo(accountFrom.getBalance()) > 0) {
                throw new BadRequestException("Insufficient balance after lock");
            }
            response.setOldBalance(accountTo.getBalance());
            debit(accountFrom, transferDTO.getBalance());
            credit(accountTo, transferDTO.getBalance());

            response.setNewBalance(accountTo.getBalance());
            response.setMessage("Successfully transferred amount from source to destination account");
        }catch (Exception e){
            throw new TransferFailedException("Amount transferred failed");
        }
        finally {
            lock2.unlock();
            lock1.unlock();
        }
        notificationService.notifyAboutTransfer(accountFrom, "Transferred " + transferDTO.getBalance() + " to account " + transferDTO.getAccountToId());
        notificationService.notifyAboutTransfer(accountTo, "Received " + transferDTO.getBalance() + " from account " + transferDTO.getAccountFromId());
        return response;
    }
}
