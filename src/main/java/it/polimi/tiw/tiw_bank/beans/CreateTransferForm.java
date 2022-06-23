package it.polimi.tiw.tiw_bank.beans;

public class CreateCurrentAccountForm {
    private Float openingBalance;
    private Integer holderId;

    public CreateCurrentAccountForm(Float openingBalance, Integer holderId) {
        this.openingBalance = openingBalance;
        this.holderId = holderId;
    }

    public Float getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Float openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Integer getHolderId() {
        return holderId;
    }

    public void setHolderId(Integer holderId) {
        this.holderId = holderId;
    }
}
