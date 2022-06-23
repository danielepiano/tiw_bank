package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.CreateCurrentAccountForm;
import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.beans.RegistrationForm;
import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.UserRoles;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.validators.BaseValidator;
import it.polimi.tiw.tiw_bank.validators.CurrentAccountValidator;
import it.polimi.tiw.tiw_bank.validators.UserValidator;

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

@WebServlet(name = "CreateCurrentAccount", value = "/create-current-account")
public class CreateCurrentAccount extends HttpServlet {
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
        request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // LETTURA PARAMETRI
        List<String> errors = new ArrayList<>();

        Float openingBalance = null;
        Integer holderId = null;
        try {
            openingBalance = Float.parseFloat( request.getParameter("opening_balance") );
        } catch ( NumberFormatException e ) {
            errors.add("Opening balance must be a floating point number.");
        }
        try {
            holderId = Integer.parseInt( request.getParameter("holder_id") );
        } catch ( NumberFormatException e ) {
            errors.add("Invalid holder.");
        }

        // VALIDAZIONE PARAMETRI
        try {
            errors.addAll( postValidation(openingBalance, holderId) );

            // 0 ERRORI: completamento servizio registrazione
            if (errors.isEmpty()) {
                // SERVIZIO E CHIAMATE AL DAO
                CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
                currentAccountDao.create(openingBalance, holderId);

                errors.add("Successful creation!");
                request.setAttribute("messages", errors);
                response.setStatus(HttpServletResponse.SC_OK);
                request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
            }
            // ERRORI: salvataggio errori e ritorno alla pagina di login
            else {
                request.setAttribute("messages", errors);
                request.setAttribute("ca_form", new CreateCurrentAccountForm(openingBalance, holderId));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        }

    }


    private List<String> postValidation(Float openingBalance, Integer holderId) throws SQLException {
        List<String> errors = new ArrayList<>();

        // Check opening balance
        try {
            BaseValidator.rule_required("opening balance", openingBalance);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check holderId
        try {
            BaseValidator.rule_required("holder", holderId);
            CurrentAccountValidator.rule_idExists(holderId, connection);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }
}
