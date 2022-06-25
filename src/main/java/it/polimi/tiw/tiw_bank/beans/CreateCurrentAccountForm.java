package it.polimi.tiw.tiw_bank.beans;

import it.polimi.tiw.tiw_bank.models.CurrentAccount;

import java.util.UUID;

public class CreateCurrentAccountForm extends CurrentAccount {
   public CreateCurrentAccountForm(Float openingBalance, Integer holderId) {
        setRandomAccountNumber();
        setOpeningBalance(openingBalance);
        super.setHolderId(holderId);
    }

    public Float getOpeningBalance() {
        return super.getBalance();
    }

    public void setOpeningBalance(Float openingBalance) {
        super.setBalance(openingBalance);
    }

    /**
     * Generazione numero conto come stringa alfanumerica di 12 caratteri.
     * @return
     */
    private String setRandomAccountNumber() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "").substring(0, 12);
    }
}
