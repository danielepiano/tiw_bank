package it.polimi.tiw.tiw_bank.controllers;

import it.polimi.tiw.tiw_bank.beans.CreateCurrentAccountForm;
import it.polimi.tiw.tiw_bank.beans.CreateTransferForm;
import it.polimi.tiw.tiw_bank.dao.CurrentAccountDAO;
import it.polimi.tiw.tiw_bank.dao.TransferDAO;
import it.polimi.tiw.tiw_bank.exceptions.ValidationException;
import it.polimi.tiw.tiw_bank.models.CurrentAccount;
import it.polimi.tiw.tiw_bank.models.Transfer;
import it.polimi.tiw.tiw_bank.models.User;
import it.polimi.tiw.tiw_bank.utils.ConnectionHandler;
import it.polimi.tiw.tiw_bank.validators.BaseValidator;
import it.polimi.tiw.tiw_bank.validators.CurrentAccountValidator;
import it.polimi.tiw.tiw_bank.validators.TransferValidator;
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

@WebServlet(name = "CreateTransfer", value = "/create-transfer")
public class CreateTransfer extends HttpServlet {
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
        response.sendRedirect("manage-current-account?id=" +
                ((CurrentAccount)request.getSession().getAttribute("picked_currentAccount")).getId() );
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lettura parametri
        List<String> errors = new ArrayList<>();
        CreateTransferForm createTransferForm = getParameters(request, errors);

        try {
            // Validazione parametri
            errors.addAll( postValidation(request, createTransferForm) );

            if (errors.isEmpty()) {
                // 0 ERRORI: completamento SERVIZIO trasferimento denaro
                Transfer committedTransfer = transferService(request, response, createTransferForm);

                // Preparazione pagina conferma trasferimento
                request.getSession().setAttribute("transfer", committedTransfer);
                response.sendRedirect("transfer-confirmation");
            } else {
                // ERRORI: salvataggio errori e ritorno alla pagina di gestione conto corrente
                request.setAttribute("messages", errors);
                request.setAttribute("transfer_form", createTransferForm);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                request.getRequestDispatcher("/customer_manage_current_account.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Oops! Internal server error, retry later...");
        }

    }


    /**
     * Servizio per trasferimento denaro.
     * Aggiunta importo al saldo del conto destinatario, scalo importo dal saldo del conto di origine, creazione
     * trasferimento. Operazione atomica.
     * @param request
     * @param response
     * @param transfer
     * @return
     * @throws SQLException
     */
    private Transfer transferService(HttpServletRequest request, HttpServletResponse response, CreateTransferForm transfer)
            throws SQLException {
        CurrentAccountDAO currentAccountDao = new CurrentAccountDAO(connection);
        TransferDAO transferDao = new TransferDAO(connection);

        try {
            // Permette di effettuare un COMMIT manuale. In caso di eccezioni, possibile effettuare un ROLLBACK.
            // Garantisce l'atomicità del trasferimento: +amount recipient, -amount sender, create transfer.
            connection.setAutoCommit(false);

            CurrentAccount senderCA = ((CurrentAccount)request.getSession().getAttribute("picked_currentAccount"));
            CurrentAccount recipientCA = currentAccountDao.retrieveByAccountNumber(transfer.getRecipientAccountNumber());

            // Aggiunta importo al saldo del recipient
            currentAccountDao.updateBalance(recipientCA.getId(), recipientCA.getBalance() + transfer.getAmount());
            // Differenza importo dal saldo del sender
            currentAccountDao.updateBalance(senderCA.getId(), senderCA.getBalance() - transfer.getAmount());
            // Creazione trasferimento
            Integer committedTransferId = transferDao.create(
                    transfer.getAmount(),
                    transfer.getReason(),
                    senderCA.getId(),
                    recipientCA.getId()
            );
            Transfer committedTransfer = transferDao.retrieveById( committedTransferId );

            // Se tutto è andato a buon fine, COMMIT per salvare un nuovo stato consistente dei dati.
            connection.commit();
                // update sender current account (per memorizzare in sessione il nuovo bilancio!)
            senderCA = currentAccountDao.retrieveById( senderCA.getId() );
            request.getSession().setAttribute("picked_currentAccount", senderCA);
            return committedTransfer;
        } catch (SQLException e) {
            // In caso di eccezioni, rollback per ritornare a uno stato consistente.
            connection.rollback();
            throw e;
        }
    }

    /**
     * Lettura parametri dalla richiesta.
     * @param request
     * @param errors
     * @return
     */
    private CreateTransferForm getParameters(HttpServletRequest request, List<String> errors) {
        Float amount = null;
        try { amount = Float.parseFloat( request.getParameter("amount") );
        } catch ( NumberFormatException e ) { errors.add("Amount must be a floating point number.");}

        String reason = request.getParameter("reason");

        Integer recipientId = null;
        try { recipientId = Integer.parseInt( request.getParameter("recipient_id") );
        } catch ( NumberFormatException e ) { errors.add("Invalid recipient code.");}

        String recipientAccountNumber = request.getParameter("recipient_account_number");

        return new CreateTransferForm(amount, reason, recipientId, recipientAccountNumber);
    }

    /**
     * Validazione parametri della richiesta.
     * @param request
     * @param transfer
     * @return
     * @throws SQLException
     */
    private List<String> postValidation(HttpServletRequest request, CreateTransferForm transfer) throws SQLException {
        List<String> errors = new ArrayList<>();

        Float amount = transfer.getAmount();
        String reason = transfer.getReason();
        Integer recipientId = transfer.getRecipientId();
        String recipientAccountNumber = transfer.getRecipientAccountNumber();

        // Check importo
        try {
            BaseValidator.rule_required("amount", amount);
            BaseValidator.rule_greaterThan("amount", amount, 0f);
            if ( amount > ((CurrentAccount)request.getSession().getAttribute("picked_currentAccount")).getBalance() ) {
                throw new ValidationException("Invalid amount: exceeding the current account balance!");
            }
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check causale
        try {
            BaseValidator.rule_required("reason", reason);
            BaseValidator.rule_minLength("reason", reason, 3);
            BaseValidator.rule_maxLength("reason", reason, 140);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check recipient id
        try {
            BaseValidator.rule_required("recipient id", recipientId);
            TransferValidator.rule_recipientIdExists(recipientId, connection);
            if ( recipientId.equals( ((User)request.getSession().getAttribute("user")).getId() ) ) {
                throw new ValidationException("Invalid recipient id: cannot use yours.");
            }
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        // Check recipient account number
        try {
            BaseValidator.rule_required("recipient account number", recipientAccountNumber);
            TransferValidator.rule_recipientAccountNumberExists(recipientAccountNumber, connection);
            TransferValidator.rule_recipientAccountNumberBelongsToRecipientId(recipientAccountNumber, recipientId, connection);
        } catch (ValidationException e) {
            errors.add(e.getMessage());
        }

        return errors;
    }
}
