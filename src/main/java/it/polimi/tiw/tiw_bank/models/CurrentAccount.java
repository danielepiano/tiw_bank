package it.polimi.tiw.tiw_bank.models;

public class CurrentAccount {

    private Integer id;
    private String accountNumber;
    private Float balance;
    private Integer holderId;


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public Float getBalance() {
        return balance;
    }
    public void setBalance(Float balance) {
        this.balance = balance;
    }
    public Integer getHolderId() {
        return holderId;
    }
    public void setHolderId(Integer holderId) {
        this.holderId = holderId;
    }


    private User holder;

    public User getHolder() {
        return holder;
    }

    public void setHolder(User holder) {
        this.holder = holder;
    }
}

