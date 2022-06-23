package it.polimi.tiw.tiw_bank.validators;

import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;

import java.sql.Connection;
import java.sql.SQLException;

public class UserValidator {

    /**
     * Check unicità email inserita dall'utente.
     * @param email
     * @param connection
     * @return
     * @throws SQLException
     * @throws ValidationException
     */
    public static boolean rule_emailUnique(String email, Connection connection) throws SQLException, ValidationException {
        UserDAO userDao = new UserDAO(connection);
        if ( userDao.retrieveByEmail(email) == null ) {
            return true;
        }
        throw new ValidationException("Email already used.");
    }
}
