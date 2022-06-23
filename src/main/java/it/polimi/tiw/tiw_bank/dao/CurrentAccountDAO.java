package it.polimi.tiw.tiw_bank.dao;

import it.polimi.tiw.tiw_bank.models.CurrentAccount;
import it.polimi.tiw.tiw_bank.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CurrentAccountDAO {
    private Connection con;

    public CurrentAccountDAO(Connection connection) {
        this.con = connection;
    }


    /**
     * Creazione conto corrente.
     * @param balance		Saldo iniziale conto corrente.
     * @param holderId		Id utente a cui è associato il c
     * @return
     * @throws SQLException
     */
    public Integer create(Float balance, Integer holderId) throws SQLException {
        String query = "INSERT into current_accounts (account_number, balance, holder_id) VALUES(?, ?, ?)";
        try ( PreparedStatement pstat = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS) ) {
            pstat.setString( 1, internal_generateAccountNumber() );
            pstat.setFloat( 2, balance );
            pstat.setInt( 3, holderId );
            pstat.executeUpdate();

            // Ritornato l'id della risorsa appena creata.
            ResultSet generatedKeys = pstat.getGeneratedKeys();
            if ( generatedKeys.next() ) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating current account failed, no ID obtained.");
            }
        }
    }

    /**
     * Retrieve conto corrente dato id.
     * @param id			        Id conto corrente richiesto.
     * @return
     * @throws SQLException
     */
    public CurrentAccount retrieveById(Integer id) throws SQLException {
        String query = "SELECT * FROM current_accounts WHERE id = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setInt( 1, id );
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) {	// Se non esiste il conto corrente, ritorna null
                    return null;
                } else {			                // Altrimenti, ritorna un oggetto CurrentAccount con i dati relativi
                    result.next();
                    return internal_getCurrentAccountByResult(result);
                }
            }
        }
    }

    /**
     * Retrieve conto corrente dato account number.
     * @param accountNumber			Account number conto corrente richiesto.
     * @return
     * @throws SQLException
     */
    public CurrentAccount retrieveByAccountNumber(String accountNumber) throws SQLException {
        String query = "SELECT * FROM current_accounts WHERE account_number = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setString( 1, accountNumber );
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) {	// Se non esiste il conto corrente, ritorna null
                    return null;
                } else {			                // Altrimenti, ritorna un oggetto CurrentAccount con i dati relativi
                    result.next();
                    return internal_getCurrentAccountByResult(result);
                }
            }
        }
    }

    /**
     * Retrieve conto corrente dato account number.
     * @param holderId		        Id conto corrente richiesto.
     * @param accountNumber			Account number conto corrente richiesto.
     * @return
     * @throws SQLException
     */
    public CurrentAccount retrieveByHolderIdAndAccountNumber(Integer holderId, String accountNumber) throws SQLException {
        String query = "SELECT * FROM current_accounts WHERE holder_id = ? AND account_number = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setInt( 1, holderId );
            pstat.setString( 2, accountNumber );
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) {	// Se non esiste il conto corrente, ritorna null
                    return null;
                } else {			                // Altrimenti, ritorna un oggetto CurrentAccount con i dati relativi
                    result.next();
                    return internal_getCurrentAccountByResult(result);
                }
            }
        }
    }

    /**
     * Retrieve conti corrente di un utente.
     * @param holderId		Id utente di cui selezionare i conti corrente.
     * @return
     */
    public List<CurrentAccount> retrieveByHolderId(Integer holderId) throws SQLException {
        List<CurrentAccount> currentAccounts = new ArrayList<>();

        String query = "SELECT * FROM current_accounts WHERE holder_id = ? ORDER BY account_number";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setInt( 1, holderId );
            try ( ResultSet result = pstat.executeQuery() ) {
                while ( result.next() ) {
                    currentAccounts.add( internal_getCurrentAccountByResult(result) );
                }
            }
        }
        return currentAccounts;
    }

    /**
     * Update saldo conto corrente.
     * @param id                    Id conto corrente da aggiornare.
     * @param balance               Importo da aggiungere/sottrarre.
     * @return
     * @throws SQLException
     */
    public Integer updateBalance(Integer id, Float balance) throws SQLException {
        String query = "UPDATE current_accounts SET balance = ? WHERE id = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setFloat( 1, balance );
            pstat.setInt(2, id);
            // Ritornato numero righe modificate
            return pstat.executeUpdate();
        }
    }


    /**
     * Ottenimento oggetto CurrentAccount a partire da una row del ResultSet.
     * @param result
     * @return
     * @throws SQLException
     */
    private CurrentAccount internal_getCurrentAccountByResult(ResultSet result) throws SQLException {
        CurrentAccount ca = new CurrentAccount();
        ca.setId( result.getInt("id") );
        ca.setBalance( result.getFloat("balance") );
        ca.setHolderId(	result.getInt("holder_id") );
        ca.setAccountNumber( result.getString("account_number") );

        UserDAO userDao = new UserDAO(con);
        ca.setHolder( userDao.retrieveById( result.getInt("holder_id") ) );

        return ca;
    }

    /**
     * Generazione numero conto come stringa alfanumerica di 12 caratteri.
     * @return
     */
    private String internal_generateAccountNumber() {
        UUID randomUUID = UUID.randomUUID();
        System.out.println( randomUUID.toString().replaceAll("-", "").substring(0, 12) );
        return randomUUID.toString().replaceAll("-", "").substring(0, 12);
    }
}

