package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.LoginForm;
import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.models.UserRoles;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.validators.BaseValidator;

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

@WebServlet(name = "CustomerHome", value = "/customer-home")
public class CustomerHome extends HttpServlet {
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
        request.getSession().removeAttribute("picked_currentAccount");

        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
        User sexUser = (User)request.getSession().getAttribute("user");

        try {
            request.setAttribute("myCurrentAccounts", currentAccountDao.retrieveByHolderId( sexUser.getId() ));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        }

        request.getRequestDispatcher("/customer_home.jsp").forward(request, response);
    }

}
