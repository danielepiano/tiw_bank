package it.polimi.tiw.tiw_bank.beans;

import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.models.UserRoles;

public class RegistrationForm extends User {
    private String confirmPassword;


    public RegistrationForm(String firstName, String lastName, String email, String password, String confirmPassword) {
        super(firstName, lastName, email, password);
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
