package it.polimi.tiw.moneytransfer.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;

public class ConnectionHandler {

	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection = null;
		try {
			String url = context.getInitParameter("dbUrl");
			String usr = context.getInitParameter("dbUser");
			String pwd = context.getInitParameter("dbPassword");
			String driver = context.getInitParameter("dbDriver");
			Class.forName(driver);
			
			connection = DriverManager.getConnection(url, usr, pwd);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Couldn't load database driver: " + e.getMessage());
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection: " + e.getMessage());
		}
		return connection;
	}

	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
	
}
