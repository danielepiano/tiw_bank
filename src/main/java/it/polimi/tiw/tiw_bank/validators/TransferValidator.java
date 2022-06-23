package it.polimi.tiw.tiw_bank.validators;

import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;

import java.sql.Connection;
import java.sql.SQLException;

public class TransferValidator {

    /**
     * Check esistenza utente dato id.
     * @param recipientId
     * @param connection
     * @return
     * @throws SQLException
     * @throws ValidationException
     */
    public static boolean rule_recipientIdExists(Integer recipientId, Connection connection) throws SQLException, ValidationException {
        UserDAO userDao = new UserDAO(connection);
        if ( userDao.retrieveById(recipientId) != null ) {
            return true;
        }
        throw new ValidationException("Non-existing user.");
    }

    /**
     * Check esistenza conto corrente dato account number.
     * @param recipientAccountNumber
     * @param connection
     * @return
     * @throws SQLException
     * @throws ValidationException
     */
    public static boolean rule_recipientAccountNumberExists(String recipientAccountNumber, Connection connection)
            throws SQLException, ValidationException {
        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
        if ( currentAccountDao.retrieveByAccountNumber( recipientAccountNumber ) != null ) {
            return true;
        }
        throw new ValidationException("Non-existing current account.");
    }

    /**
     * Check corrispondenza conto corrente - utente, dati idUtente e numeroContoCorrente.
     * @param recipientAccountNumber
     * @param recipientId
     * @param connection
     * @return
     * @throws SQLException
     * @throws ValidationException
     */
    public static boolean rule_recipientAccountNumberBelongsToRecipientId(String recipientAccountNumber,
                                                                          Integer recipientId, Connection connection)
            throws SQLException, ValidationException {
        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
        if ( currentAccountDao.retrieveByHolderIdAndAccountNumber(recipientId, recipientAccountNumber) != null ) {
            return true;
        }
        throw new ValidationException("Current account not belonging to the specified user.");
    }
}
