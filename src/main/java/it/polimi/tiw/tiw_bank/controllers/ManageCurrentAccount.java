package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.CreateCurrentAccountForm;
import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.TransferDAO;
import it.polimi.tiw.tiw_bank.exceptions.UnauthorizedException;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.CurrentAccount;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.validators.BaseValidator;
import it.polimi.tiw.tiw_bank.validators.CurrentAccountValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = " ManageCurrentAccount", value = "/manage-current-account")
public class ManageCurrentAccount extends HttpServlet {
    protected Connection connection = null;

    @Override
    public void init() throws ServletException {
        super.init();
        connection = ConnectionHandler.getConnection( getServletContext() );
    }

    @Override
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.destroy();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().removeAttribute("transfer");

        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
        TransferDAO transferDao = new TransferDAO(connection);

        try {
            User sexUser = (User)request.getSession().getAttribute("user");

            // Lettura id conto corrente da gestire, specificato nella query string
            Integer id = Integer.parseInt( request.getParameter("id") );
            CurrentAccount pickedCurrentAccount = currentAccountDao.retrieveById(id);
            request.getSession().setAttribute("picked_currentAccount", pickedCurrentAccount);

            // Check che il conto corrente da gestire sia effettivamente dell'utente in sessione
            if ( !pickedCurrentAccount.getHolderId().equals( sexUser.getId() ) ) {
                throw new UnauthorizedException();
            }

            // Caricamento in sessione storico trasferimenti per il conto selezionato
            request.getSession().setAttribute("transfer_history", transferDao.retrieveByCurrentAccountId( id ));
            response.setStatus(HttpServletResponse.SC_OK);
            request.getRequestDispatcher("/customer_manage_current_account.jsp").forward(request, response);
        } catch ( SQLException e ) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        } catch ( NumberFormatException | UnauthorizedException e ) {
            response.sendRedirect("customer-home");
        }
    }

}
