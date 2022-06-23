package it.polimi.tiw.tiw_bank.beans;

public class LoginRegisterForm {
    private String email;
    private String password;
    private String confirmPassword;
    
    public LoginRegisterForm() {}

    public LoginRegisterForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginRegisterForm(String email, String password, String confirmPassword) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
