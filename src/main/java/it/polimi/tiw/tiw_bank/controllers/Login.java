package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.validators.BaseValidator;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "Login", value = "/login")
public class Login extends HttpServlet {
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
        request.setAttribute("messages", request.getSession().getAttribute("messages") );
        request.getSession().removeAttribute("messages");

        request.setAttribute("login_form", request.getSession().getAttribute("login_form") );
        request.getSession().removeAttribute("login_form");

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO userDao = new UserDAO(connection);

        // Lettura parametri
        List<String> errors = new ArrayList<>();
        LoginForm loginForm = getParameters(request, errors);

        try {
            // Validazione parametri
            errors.addAll( postValidation(request, loginForm) );

            if (errors.isEmpty()) {
                // 0 ERRORI: completamento SERVIZIO login
                User user = userDao.checkCredentials( loginForm.getEmail(), loginForm.getPassword() );

                if (user == null) {
                    // Se login fallito, invio errore
                    errors.add("Incorrect credentials.");
                    request.setAttribute("messages", errors);
                    request.setAttribute("login_form", loginForm);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                } else {
                    // Altrimenti, salvataggio utente in sessione e indirizza a HOME in base al ruolo dell'utente
                    request.getSession().setAttribute("user", user);
                    switch ( user.getRole() ) {
                        case ADMIN:
                            response.sendRedirect("admin-home");
                            break;
                        case CUSTOMER:
                            response.sendRedirect("customer-home");
                    }
                }
            } else {
                // ERRORI: salvataggio errori e ritorno alla pagina di LOGIN
                request.setAttribute("messages", errors);
                request.setAttribute("login_form", loginForm);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
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
    private LoginForm getParameters(HttpServletRequest request, List<String> errors) {
        return new LoginForm( request.getParameter("email"), request.getParameter("password") );
    }

    /**
     * Validazione parametri della richiesta.
     * @param form
     * @return
     */
    private List<String> postValidation(HttpServletRequest request, LoginForm form) {
        List<String> errors = new ArrayList<>();

        String email = form.getEmail();
        String password = form.getPassword();

        // Check email compilata
        try {
            BaseValidator.rule_required("email", email);
            BaseValidator.rule_validEmail(email);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check password compilata
        try {
            BaseValidator.rule_required("password", password);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }

}
