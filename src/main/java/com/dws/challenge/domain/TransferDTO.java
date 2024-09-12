package com.dws.challenge.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferDTO {

    @NotNull
    @NotEmpty
    private String accountFromId;
    @NotNull
    @NotEmpty
    private String accountToId;
    @NotNull
    @Min(value = 0, message = "Initial balance must be positive.")
    private BigDecimal balance;

    public TransferDTO(String accountFromId, String accountToId, BigDecimal balance) {
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.balance = balance;
    }

}
