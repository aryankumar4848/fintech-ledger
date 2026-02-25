package com.aryan.fintech_ledger.dto;

import java.math.BigDecimal;

public class WithdrawRequest {
    private BigDecimal amount;
    private String idempotencyKey;

    public WithdrawRequest() {
    }

    public WithdrawRequest(BigDecimal amount, String idempotencyKey) {
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
