package it.polimi.tiw.tiw_bank.dao;

import it.polimi.tiw.tiw_bank.models.Transfer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransferDAO {
    private Connection con;

    public TransferDAO(Connection connection) {
        this.con = connection;
    }


    /**
     * Creazione trasferimento.
     * @param amount
     * @param reason
     * @param senderAccountId
     * @param recipientAccountId
     * @return
     * @throws SQLException
     */
    public Integer create(Float amount, String reason, Integer senderAccountId, Integer recipientAccountId)
            throws SQLException {
        String query = "INSERT into transfers (amount, reason, issue_date, sender_account_id, recipient_account_id)"
                + " VALUES(?, ?, ?, ?, ?)";
        try ( PreparedStatement pstat = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS) ) {
            pstat.setFloat( 1, amount );
            pstat.setString( 2, reason );
            pstat.setDate( 3, Date.valueOf(LocalDate.now()) );
            pstat.setInt( 4, senderAccountId );
            pstat.setInt( 5, recipientAccountId );
            pstat.executeUpdate();

            // Ritornato l'id della risorsa appena creata.
            ResultSet generatedKeys = pstat.getGeneratedKeys();
            if ( generatedKeys.next() ) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating transfer failed, no ID obtained.");
            }
        }
    }

    /**
     * Retrieve trasferimento dato id.
     * @param id				Id trasferimento richiesto.
     * @return
     * @throws SQLException
     */
    public Transfer retrieveById(Integer id) throws SQLException {
        String query = "SELECT * FROM transfers WHERE id = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setInt( 1, id );
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) { 		// Se non esiste il trasferimento, ritorna null
                    return null;
                } else {				// Altrimenti, ritorna un oggetto Transfer con i dati relativi
                    result.next();
                    return internal_getTransferByResult(result);
                }
            }
        }
    }

    /**
     * Retrieve trasferimenti di un conto corrente.
     * @param currentAccountId		Id conto corrente di cui selezionare i trasferimenti.
     * @return
     */
    public List<Transfer> retrieveByCurrentAccountId(Integer currentAccountId) throws SQLException {
        List<Transfer> transfers = new ArrayList<>();

        String query = "SELECT * FROM transfers WHERE sender_account_id = ?"
                + " OR recipient_account_id = ?"
                + " ORDER BY issue_date DESC";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setInt( 1, currentAccountId );
            pstat.setInt( 2, currentAccountId );
            try ( ResultSet result = pstat.executeQuery() ) {
                while ( result.next() ) {
                    transfers.add( internal_getTransferByResult(result) );
                }
            }
        }
        return transfers;
    }



    /**
     * Ottenimento oggetto Transfer a partire da una row del ResultSet.
     * @param result
     * @return
     * @throws SQLException
     */
    private Transfer internal_getTransferByResult(ResultSet result) throws SQLException {
        Transfer transfer = new Transfer();
        transfer.setId( result.getInt("id") );
        transfer.setAmount( result.getFloat("amount") );
        transfer.setReason( result.getString("reason") );
        transfer.setIssueDate( result.getDate("issue_date").toLocalDate() );
        transfer.setSenderAccountId( result.getInt("sender_account_id") );
        transfer.setRecipientAccountId( result.getInt("recipient_account_id") );

        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(con);
        transfer.setSenderAccount( currentAccountDao.retrieveById( result.getInt("sender_account_id") ) );
        transfer.setRecipientAccount( currentAccountDao.retrieveById( result.getInt("recipient_account_id") ) );

        return transfer;
    }

}

