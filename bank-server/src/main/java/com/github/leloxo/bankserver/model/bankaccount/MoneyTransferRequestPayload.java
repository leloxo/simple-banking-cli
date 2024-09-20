package com.github.leloxo.bankserver.model.bankaccount;

import java.math.BigDecimal;

public class MoneyTransferRequestPayload {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private BigDecimal amount;

    public MoneyTransferRequestPayload() {}

    public MoneyTransferRequestPayload(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) {
        this.senderAccountNumber = senderAccountNumber;
        this.receiverAccountNumber = receiverAccountNumber;
        this.amount = amount;
    }

    public String getSenderAccountNumber() {
        return senderAccountNumber;
    }

    public void setSenderAccountNumber(String senderAccountNumber) {
        this.senderAccountNumber = senderAccountNumber;
    }

    public String getReceiverAccountNumber() {
        return receiverAccountNumber;
    }

    public void setReceiverAccountNumber(String receiverAccountNumber) {
        this.receiverAccountNumber = receiverAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
