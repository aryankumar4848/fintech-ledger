package com.aryan.fintech_ledger.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private Long toAccountId;
    private BigDecimal amount;
    private String idempotencyKey;

    public TransferRequest() {
    }

    public TransferRequest(Long toAccountId, BigDecimal amount, String idempotencyKey) {
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
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
