package it.polimi.tiw.tiw_bank.validators;

import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;

import java.sql.Connection;
import java.sql.SQLException;

public class CurrentAccountValidator {

    /**
     * Check esistenza utente dato id.
     * @param holderId
     * @param connection
     * @return
     * @throws SQLException
     * @throws ValidationException
     */
    public static boolean rule_idExists(Integer holderId, Connection connection) throws SQLException, ValidationException {
        UserDAO userDao = new UserDAO(connection);
        if ( userDao.retrieveById(holderId) != null ) {
            return true;
        }
        throw new ValidationException("Non-existing user.");
    }
}
