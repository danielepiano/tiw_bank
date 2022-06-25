package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.CreateCurrentAccountForm;
import it.polimi.tiw.tiw_bank.beans.CreateTransferForm;
import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.beans.RegistrationForm;
import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.CurrentAccount;
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
        response.sendRedirect("admin-home");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);

        // Lettura parametri
        List<String> errors = new ArrayList<>();
        CreateCurrentAccountForm createCAForm = getParameters(request, errors);

        try {
            // Validazione parametri
            errors.addAll( postValidation(request, createCAForm) );

            if (errors.isEmpty()) {
                // 0 ERRORI: completamento SERVIZIO creazione conto corrente
                // SERVIZIO E CHIAMATE AL DAO
                currentAccountDao.create( createCAForm );

                // Preparazione pagina conferma creazione conto corrente (coincide con la ADMIN HOME stessa)
                errors.add("Successful creation!");
                request.getSession().setAttribute("messages", errors);
                response.sendRedirect("admin-home");
            }
            else {
                // ERRORI: salvataggio errori e ritorno alla pagina ADMIN-HOME
                request.setAttribute("messages", errors);
                request.setAttribute("ca_form", createCAForm);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        }

    }


    /**
     * Lettura parametri dalla richiesta.
     * @param request
     * @param errors
     * @return
     */
    private CreateCurrentAccountForm getParameters(HttpServletRequest request, List<String> errors) {
        Float openingBalance = null;
        try { openingBalance = Float.parseFloat( request.getParameter("opening_balance") );
        } catch ( NumberFormatException e ) { errors.add("Opening balance must be a floating point number.");}

        Integer holderId = null;
        try { holderId = Integer.parseInt( request.getParameter("holder_id") );
        } catch ( NumberFormatException e ) { errors.add("Invalid holder.");}

        return new CreateCurrentAccountForm(openingBalance, holderId);
    }

    /**
     * Validazione parametri della richiesta.
     * @param request
     * @param currentAccount
     * @return
     * @throws SQLException
     */
    private List<String> postValidation(HttpServletRequest request, CreateCurrentAccountForm currentAccount)
            throws SQLException {
        List<String> errors = new ArrayList<>();

        Float openingBalance = currentAccount.getOpeningBalance();
        Integer holderId = currentAccount.getHolderId();

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
