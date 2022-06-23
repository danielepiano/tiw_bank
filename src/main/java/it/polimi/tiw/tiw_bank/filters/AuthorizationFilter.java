package it.polimi.tiw.tiw_bank.filters;

import it.polimi.tiw.tiw_bank.controllers.*;
import it.polimi.tiw.tiw_bank.models.User;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = "*")
public class AuthenticationFilter implements Filter {
    private final List<String> customerJSP = Arrays.asList(
            "customer_home.jsp",
            "customer_manage_current_account.jsp",
            "customer_transfer_confirm.jsp"
    );
    private final List<String> adminJSP = Arrays.asList(
            "admin_home.jsp"
    );
    private final List<String> publicJSP = Arrays.asList(
            "login.jsp",
            "register.jsp"
    );

    private final List<String> customerServlet = Arrays.asList(
            Stream.of(
                    CustomerHome.class.getAnnotation( WebServlet.class ).value(),
                    ManageCurrentAccount.class.getAnnotation( WebServlet.class ).value(),
                    CreateTransfer.class.getAnnotation( WebServlet.class ).value(),
                    TransferConfirmation.class.getAnnotation( WebServlet.class ).value(),
                    Logout.class.getAnnotation( WebServlet.class ).value()
            ).flatMap( Stream::of ).toArray( String[]::new )
    );
    private final List<String> adminServlet = Arrays.asList(
            Stream.of(
                    AdminHome.class.getAnnotation( WebServlet.class ).value(),
                    CreateCurrentAccount.class.getAnnotation( WebServlet.class ).value(),
                    Logout.class.getAnnotation( WebServlet.class ).value()
            ).flatMap( Stream::of ).toArray( String[]::new )
    );
    private final List<String> publicServlet = Arrays.asList(
            Stream.of(
                    Login.class.getAnnotation( WebServlet.class ).value(),
                    Logout.class.getAnnotation( WebServlet.class ).value(),
                    Register.class.getAnnotation( WebServlet.class ).value()
            ).flatMap( Stream::of ).toArray( String[]::new )
    );


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        User sexUser = (User)req.getSession().getAttribute("user");

        try {
            switch ( sexUser.getRole() ) {
                case ADMIN:
                    // Se ADMIN loggato, ma la richiesta non è su una JSP o su una Servlet ADMIN, torna alla ADMIN HOME
                    if ( adminJSP.stream().noneMatch(uri::endsWith) && adminServlet.stream().noneMatch(uri::endsWith)) {
                        res.sendRedirect("admin-home");
                    } else {
                        chain.doFilter(request, response);
                    }
                    break;
                case CUSTOMER:
                    // Se CUSTOMER loggato, ma la richiesta non è su una JSP o su una Servlet CUSTOMER, torna alla CUSTOMER HOME
                    if ( customerJSP.stream().noneMatch(uri::endsWith) && customerServlet.stream().noneMatch(uri::endsWith)) {
                        res.sendRedirect("customer-home");
                    } else {
                        chain.doFilter(request, response);
                    }
            }
        } catch ( NullPointerException e ) { // Nessun utente loggato
            // Se nessun utente loggato, ma la richiesta non è su una JSP o su una Servlet PUBLIC, torna alla LOGIN
            if ( publicJSP.stream().noneMatch(uri::endsWith) && publicServlet.stream().noneMatch(uri::endsWith)) {
                res.sendRedirect("login");
            } else {
                chain.doFilter(request, response);
            }
        }

    }
}
