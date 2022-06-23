package it.polimi.tiw.tiw_bank.models;

public enum UserRoles {
    ADMIN("admin"), CUSTOMER("customer");

    private final String role;

    UserRoles(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public static UserRoles getUserRoleFromString(String role) {
        switch (role) {
            case "admin":
                return UserRoles.ADMIN;
            case "customer":
                return UserRoles.CUSTOMER;
        }
        return null;
    }
}
