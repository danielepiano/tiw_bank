package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.beans.RegistrationForm;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.UserRoles;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.validators.BaseValidator;
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

@WebServlet(name = "Register", value = "/register")
public class Register extends HttpServlet {
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
        // Utente in sessione: reindirizzato a HOME.JSP
        if ( request.getSession().getAttribute("user") != null ) {
            request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
        }
        // Utente non in sessione: registrazione tramite REGISTER.JSP
        else {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // LETTURA PARAMETRI
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirm_password");

        // VALIDAZIONE PARAMETRI
        try {
            List<String> errors = postValidation(firstName, lastName, email, password, confirmPassword);

            // 0 ERRORI: completamento servizio registrazione
            if (errors.isEmpty()) {
                // SERVIZIO E CHIAMATE AL DAO
                UserDAO userDao = new UserDAO(connection);
                userDao.create(firstName, lastName, UserRoles.CUSTOMER, email, password);

                errors.add("Successful registration!");
                request.setAttribute("messages", errors);
                request.setAttribute("login_form", new LoginForm(email, ""));
                response.setStatus(HttpServletResponse.SC_OK);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
            // ERRORI: salvataggio errori e ritorno alla pagina di login
            else {
                request.setAttribute("messages", errors);
                request.setAttribute("reg_form", new RegistrationForm(firstName, lastName, email, password, confirmPassword));
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        }

    }


    private List<String> postValidation(String firstName, String lastName, String email, String password, String confirmPassword) throws SQLException {
        List<String> errors = new ArrayList<>();

        // Check firstname
        try {
            BaseValidator.rule_required("first name", firstName);
            BaseValidator.rule_alpha("first name", firstName);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check lastname
        try {
            BaseValidator.rule_required("last name", lastName);
            BaseValidator.rule_alpha("last name", lastName);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check email
        try {
            BaseValidator.rule_required("email", email);
            BaseValidator.rule_validEmail(email);
            UserValidator.rule_emailUnique(email, connection);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check password
        try {
            BaseValidator.rule_required("password", password);
            BaseValidator.rule_validPassword(password);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check conferma password e matching con password
        try {
            BaseValidator.rule_required("password confirmation", confirmPassword);
            BaseValidator.rule_matchingPassword(password, confirmPassword);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }
}
