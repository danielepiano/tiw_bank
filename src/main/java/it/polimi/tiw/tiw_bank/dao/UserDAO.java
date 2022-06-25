package it.polimi.tiw.tiw_bank.dao;

import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.models.UserRoles;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection con;

    public UserDAO(Connection connection) {
        this.con = connection;
    }


    /**
     * Creazione utente.
     * @param toCreate
     * @return
     * @throws SQLException
     */
    public Integer create(User toCreate) throws SQLException {
        String query = "INSERT into users (first_name, last_name, role, email, password) VALUES(?, ?, ?, ?, ?)";
        try ( PreparedStatement pstat = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS) ) {
            pstat.setString( 1, toCreate.getFirstName() );
            pstat.setString( 2, toCreate.getLastName() );
            pstat.setString( 3, toCreate.getRole().getRole() );
            pstat.setString( 4, toCreate.getEmail() );
            pstat.setString( 5, toCreate.getPassword() );
            pstat.executeUpdate();

            // Ritornato l'id della risorsa appena creata.
            ResultSet generatedKeys = pstat.getGeneratedKeys();
            if ( generatedKeys.next() ) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        }
    }

    /**
     * Retrieve utente dato id.
     * @param id				Id utente richiesto.
     * @return
     * @throws SQLException
     */
    public User retrieveById(Integer id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setInt( 1, id );
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) { 		// Se non esiste il trasferimento, ritorna null
                    return null;
                } else {				// Altrimenti, ritorna un oggetto Transfer con i dati relativi
                    result.next();
                    return internal_getUserByResult(result);
                }
            }
        }
    }

    /**
     * Retrieve utenti dato ruolo.
     * @param role
     * @return
     * @throws SQLException
     */
    public List<User> retrieveByRole(UserRoles role) throws SQLException {
        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users where role = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setString( 1, role.getRole() );
            try ( ResultSet result = pstat.executeQuery() ) {
                while ( result.next() ) {
                    users.add( internal_getUserByResult(result) );
                }
            }
        }
        return users;
    }

    /**
     * Retrieve utente data email.
     * @param email			    Email utente richiesto
     * @return
     * @throws SQLException
     */
    public User retrieveByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setString( 1, email );
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) {	// Se non esiste il conto corrente, ritorna null
                    return null;
                } else {			                // Altrimenti, ritorna un oggetto CurrentAccount con i dati relativi
                    result.next();
                    return internal_getUserByResult(result);
                }
            }
        }
    }

    /**
     * Check credenziali email-password.
     * @param attempt
     * @return
     * @throws SQLException
     */
    public User checkCredentials(LoginForm attempt) throws SQLException {
        String query = "SELECT  id, first_name, last_name, email, role FROM users WHERE email = ? AND password = ?";
        try ( PreparedStatement pstat = con.prepareStatement(query) ) {
            pstat.setString(1, attempt.getEmail());
            pstat.setString(2, attempt.getPassword());
            try ( ResultSet result = pstat.executeQuery() ) {
                if ( !result.isBeforeFirst() ) {    // Se email-password non sono associati ad alcun utente, ritorna null
                    return null;
                } else { 		                    // Altrimenti, ritorna un oggetto User con i dati relativi
                    result.next();
                    return internal_getUserByResult(result);
                }
            }
        }
    }
    
    /**
     * Ottenimento oggetto User a partire da una row del ResultSet.
     * @param result
     * @return
     * @throws SQLException
     */
    private User internal_getUserByResult(ResultSet result) throws SQLException {
        User user = new User();
        user.setId( 		result.getInt("id") );
        user.setFirstName(	result.getString("first_name") );
        user.setLastName(	result.getString("last_name") );
        user.setEmail( 		result.getString("email") );
        user.setRole( 		result.getString("role") );
        return user;
    }

}
