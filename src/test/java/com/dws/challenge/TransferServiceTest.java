package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransferDTO;
import com.dws.challenge.domain.TransferResponse;
import com.dws.challenge.exception.BadRequestException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private TransferService transferService;

    @Mock
    private NotificationService notificationService;

    @BeforeAll
    public void setUp() {
        Account accountFrom = new Account("Id-123");
        accountFrom.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(accountFrom);

        Account accountTo = new Account("Id-124");
        accountTo.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(accountTo);
    }

    @Test
    public void testTransferToNonPositiveMoney() {
        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-124", new BigDecimal("-200"));
        Exception exception = assertThrows(BadRequestException.class, () -> {
            transferService.transferMoney(transferDTO);
        });
        assertEquals("Transfer amount must be positive", exception.getMessage());
     }

    @Test
    public void testTransferToNonAccountFromId() {
        TransferDTO transferDTO = new TransferDTO("Id-1233", "Id-124", new BigDecimal("200"));
        Exception exception = assertThrows(BadRequestException.class, () -> {
            transferService.transferMoney(transferDTO);
        });
        assertEquals("AccountFromId doesn't exist", exception.getMessage());
    }

    @Test
    public void testTransferToNonAccountToId() {
        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-1244", new BigDecimal("200"));
        Exception exception = assertThrows(BadRequestException.class, () -> {
            transferService.transferMoney(transferDTO);
        });
        assertEquals("AccountToId doesn't exist", exception.getMessage());
    }

    @Test
    public void testSuccessfulTransfer() throws BadRequestException {
        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-124", new BigDecimal("200"));

        TransferResponse response = transferService.transferMoney(transferDTO);
        assertEquals(new BigDecimal("800"), accountsService.getAccount("Id-123").getBalance());
        assertEquals(new BigDecimal("1200"), accountsService.getAccount("Id-124").getBalance());

        assertEquals("Successfully transferred amount from source to destination account", response.getMessage());

    }

    @Test
    public void testTransferWithInsufficientFunds() {
        TransferDTO transferDTO = new TransferDTO("Id-123", "Id-124", new BigDecimal("20000"));

        Exception exception = assertThrows(BadRequestException.class, () -> {
            transferService.transferMoney(transferDTO);
        });
        assertEquals("Insufficient balance", exception.getMessage());
    }

    @Test
    public void testConcurrentTransfers() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        TransferDTO transferDTO1 = new TransferDTO("Id-123", "Id-124", new BigDecimal("100"));
        TransferDTO transferDTO2 = new TransferDTO("Id-124", "Id-123", new BigDecimal("100"));

        executorService.submit(() -> {
            try {
                transferService.transferMoney(transferDTO1);
            } catch (BadRequestException e) {
                fail("Unexpected exception: " + e.getMessage());
            }
        });

        executorService.submit(() -> {
            try {
                transferService.transferMoney(transferDTO2);
            } catch (BadRequestException e) {
                fail("Unexpected exception: " + e.getMessage());
            }
        });

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

    }
}
