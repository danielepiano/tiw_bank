package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.models.UserRoles;
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
        // Utente in sessione: reindirizzato a HOME.JSP
        User sexUser = (User)request.getSession().getAttribute("user");
        if ( sexUser != null ) {
            switch ( sexUser.getRole() ) {
                case ADMIN:
                    request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
                    break;
                case CUSTOMER:
                    request.getRequestDispatcher("/customer_home.jsp").forward(request, response);
            }
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
                    errors.add("Incorrect credentials.");
                    request.setAttribute("messages", errors);
                    request.setAttribute("login_form", new LoginForm(email, password));
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    request.getRequestDispatcher("/login.jsp").forward(request, response);
                }
                // Altrimenti, salvataggio utente in sessione e indirizza a HOME in base al ruolo dell'utente
                else {
                    request.getSession().setAttribute("user", user);
                    response.setStatus(HttpServletResponse.SC_OK);
                    switch ( user.getRole() ) {
                        case ADMIN:
                            request.getSession().setAttribute("holders", userDao.retrieveByRole( UserRoles.CUSTOMER ));
                            response.sendRedirect("create-current-account");
                            break;
                        case CUSTOMER:
                            CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
                            request.getSession().setAttribute("myCurrentAccounts",
                                    currentAccountDao.retrieveByHolderId( user.getId() ));
                            request.getRequestDispatcher("/customer_home.jsp").forward(request, response);
                    }
                }
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println("Oops! Internal server error, retry later...");
            }
        }
        // ERRORI: salvataggio errori e ritorno alla pagina di login
        else {
            request.setAttribute("messages", errors);
            request.setAttribute("login_form", new LoginForm(email, password));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }

    }



    private List<String> postValidation(String email, String password) {
        List<String> errors = new ArrayList<>();

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
