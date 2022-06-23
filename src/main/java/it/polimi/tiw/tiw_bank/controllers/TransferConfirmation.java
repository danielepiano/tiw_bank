package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.models.CurrentAccount;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TransferConfirmation", value = "/transfer-confirmation")
public class TransferConfirmation extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ( request.getSession().getAttribute("transfer") == null ) {
            response.sendRedirect("manage-current-account?id=" +
                    ((CurrentAccount)request.getSession().getAttribute("picked_currentAccount")).getId() );
        }
        else {
            request.getRequestDispatcher("/customer_transfer_confirm.jsp").forward(request, response);
        }
    }

}
