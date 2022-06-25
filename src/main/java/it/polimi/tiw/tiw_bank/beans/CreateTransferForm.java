package it.polimi.tiw.tiw_bank.beans;

import it.polimi.tiw.tiw_bank.controllers.CreateTransfer;
import it.polimi.tiw.tiw_bank.models.Transfer;

public class CreateTransferForm {
    private Float amount;
    private String reason;
    private Integer recipientId;
    private String recipientAccountNumber;

    public CreateTransferForm(Float amount, String reason, Integer recipientId, String recipientAccountNumber) {
        this.amount = amount;
        this.reason = reason;
        this.recipientId = recipientId;
        this.recipientAccountNumber = recipientAccountNumber;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientAccountNumber() {
        return recipientAccountNumber;
    }

    public void setRecipientAccountNumber(String recipientAccountNumber) {
        this.recipientAccountNumber = recipientAccountNumber;
    }

}
