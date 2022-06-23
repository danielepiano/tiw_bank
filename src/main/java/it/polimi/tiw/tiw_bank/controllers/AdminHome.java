package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.dao.UserDAO;
import it.polimi.tiw.tiw_bank.models.UserRoles;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "AdminHome", value = "/admin-home")
public class AdminHome extends HttpServlet {
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
        UserDAO userDao = new UserDAO(connection);

        request.setAttribute("messages", request.getSession().getAttribute("messages") );
        request.getSession().removeAttribute("messages");

        try {
            request.setAttribute("holders", userDao.retrieveByRole( UserRoles.CUSTOMER ));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        }

        request.getRequestDispatcher("/admin_home.jsp").forward(request, response);
    }

}
