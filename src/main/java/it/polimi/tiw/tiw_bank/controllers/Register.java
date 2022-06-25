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
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO userDao = new UserDAO(connection);

        // Lettura parametri
        List<String> errors = new ArrayList<>();
        RegistrationForm regForm = getParameters(request, errors);

        try {
            // Validazione parametri
            errors.addAll( postValidation(request, regForm) );

            if (errors.isEmpty()) {
                // 0 ERRORI: completamento SERVIZIO registrazione
                regForm.setRole( UserRoles.CUSTOMER );
                userDao.create( regForm );

                // Preparazione pagina LOGIN
                errors.add("Successful registration!");
                request.getSession().setAttribute("messages", errors);
                request.getSession().setAttribute("login_form", new LoginForm( regForm.getEmail(), "" ));
                response.sendRedirect("login");
            } else {
                // ERRORI: salvataggio errori e ritorno alla pagina di login
                request.setAttribute("messages", errors);
                request.setAttribute("reg_form", regForm);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
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
    private RegistrationForm getParameters(HttpServletRequest request, List<String> errors) {
        return new RegistrationForm(
                request.getParameter("first_name"),
                request.getParameter("last_name"),
                request.getParameter("email"),
                request.getParameter("password"),
                request.getParameter("confirm_password")
        );
    }

    /**
     * Validazione parametri della richiesta.
     * @param request
     * @param form
     * @return
     * @throws SQLException
     */
    private List<String> postValidation(HttpServletRequest request, RegistrationForm form) throws SQLException {
        List<String> errors = new ArrayList<>();

        String firstName = form.getFirstName();
        String lastName = form.getLastName();
        String email = form.getEmail();
        String password = form.getPassword();
        String confirmPassword = form.getConfirmPassword();

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
