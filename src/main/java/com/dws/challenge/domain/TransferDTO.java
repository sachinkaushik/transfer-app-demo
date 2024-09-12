package com.dws.challenge.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    private String accountFromId;
    private String accountToId;
    private BigDecimal balance;

    public TransferDTO(String accountFromId, String accountToId, BigDecimal balance) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.balance = balance;
    }

    public String getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(String accountFromId) {
        this.accountFromId = accountFromId;
    }

    public String getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(String accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
