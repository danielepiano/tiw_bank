package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.LoginRegisterForm;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.utils.Validator;

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
        // Utente in sessione: reindirizzato a HOME.JSP
        if ( request.getSession().getAttribute("user") != null ) {
            request.getRequestDispatcher("/home.jsp").forward(request, response);
        }
        // Utente non in sessione: login tramite LOGIN.JSP
        else {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // LETTURA PARAMETRI
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // VALIDAZIONE PARAMETRI
        List<String> errors = postValidation(email, password);

        // 0 ERRORI: completamento servizio login
        if (errors.isEmpty()) {
            // SERVIZIO E CHIAMATE AL DAO
            try {
                UserDAO userDao = new UserDAO(connection);
                User user = userDao.checkCredentials(email, password);

                // Se login fallito, invio errore
                if (user == null) {
                    errors.add("Incorrect credentials");
                    request.setAttribute("messages", errors);
                    request.setAttribute("login_form", new LoginRegisterForm(email, password));
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                // Altrimenti, salvataggio utente in sessione e indirizza a HOME PAGE
                else {
                    request.getSession().setAttribute("user", user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    // ...
                }
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Oops! Internal server error, retry later...");
            }
        }
        // ERRORI: salvataggio errori e ritorno alla pagina di login
        else {
            request.setAttribute("messages", errors);
            request.setAttribute("login_form", new LoginRegisterForm(email, password));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }

    }



    private List<String> postValidation(String email, String password) {
        List<String> errors = new ArrayList<>();

        // Check email compilata
        try {
            Validator.rule_required("Email", email);
            Validator.rule_validEmail(email);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check password compilata
        try {
            Validator.rule_required("Password", password);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }

}
