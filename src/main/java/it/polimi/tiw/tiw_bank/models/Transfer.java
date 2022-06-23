package it.polimi.tiw.tiw_bank.models;

import java.time.LocalDate;

public class Transfer {

    private Integer id;
    private Float amount;
    private String reason;
    private LocalDate issueDate;
    private Integer senderAccountId;
    private Integer recipientAccountId;


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public LocalDate getIssueDate() {
        return issueDate;
    }
    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
    public Integer getSenderAccountId() {
        return senderAccountId;
    }
    public void setSenderAccountId(Integer senderAccountId) {
        this.senderAccountId = senderAccountId;
    }
    public Integer getRecipientAccountId() {
        return recipientAccountId;
    }
    public void setRecipientAccountId(Integer recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }



    private CurrentAccount senderAccount;
    private CurrentAccount recipientAccount;

    public CurrentAccount getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(CurrentAccount senderAccount) {
        this.senderAccount = senderAccount;
    }

    public CurrentAccount getRecipientAccount() {
        return recipientAccount;
    }

    public void setRecipientAccount(CurrentAccount recipientAccount) {
        this.recipientAccount = recipientAccount;
    }
}

