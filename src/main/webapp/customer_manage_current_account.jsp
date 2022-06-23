<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
    <title>MANAGE <c:out value="${picked_currentAccount.accountNumber}" /></title>
    <style>
        table { font-family: arial, sans-serif; border-collapse: collapse;}
        td, th { border: 1px solid #dddddd; text-align: left; padding: 8px;}
        tr:nth-child(even) { background-color: #dddddd;}
    </style>
</head>
<body>

    <p><a href="logout">logout</a></p>
    <p>← Back to <a href="customer-home">home</a></p>

    <h1>Current account status</h1>
    <table>
        <tr>
            <th>User code</th>
            <th>Account number</th>
            <th>Balance</th>
        </tr>
        <tr>
            <td><c:out value="${picked_currentAccount.holderId}" /></td>
            <td><c:out value="${picked_currentAccount.accountNumber}" /></td>
            <td><fmt:formatNumber type="currency" currencySymbol="€" value="${picked_currentAccount.balance}" /></td>
        </tr>
    </table> <br><br>

    <div style="padding: 10px; border-radius: 3px; border: solid 1px">
        <h3>ORDER MONEY TRANSFER</h3>
        <form action="create-transfer" method="POST">
            <label for="amount">Amount</label> <br>
            <input type="number" id="amount" name="amount" min="0" step="0.01"
                   value="<c:out value="${transfer_form.amount}"/>" placeholder="Enter amount" required /> <br>

            <label for="reason">Reason</label> <br>
            <input type="text" id="reason" name="reason" maxlength="140" size="70"
                   value="<c:out value="${transfer_form.reason}"/>" placeholder="Enter reason" required /> <br>

            <label for="recipient_id">Recipient - code</label> <br>
            <input type="text" id="recipient_id" name="recipient_id"
                   value="<c:out value="${transfer_form.recipientId}"/>" placeholder="Enter recipient id" required /> <br>

            <label for="recipient_account_number">Recipient - account number</label> <br>
            <input type="text" id="recipient_account_number" name="recipient_account_number" size="30"
                   value="<c:out value="${transfer_form.recipientAccountNumber}"/>"
                   placeholder="Enter recipient account number" required /> <br><br>
            <input type="submit" value="Order">
        </form>

        <ul>
            <c:forEach var="error" items="${messages}">
                <li><c:out value="${error}" /></li>
            </c:forEach>
        </ul>
    </div> <br>

    <h1>Transfer history</h1>
    <c:if test="${not empty transfer_history}">
    <table >
        <tr>
            <th>Amount</th> <th>Reason</th> <th>Issue date</th>
            <th>Sender - user code</th>     <th>Sender - full name</th>    <th>Sender - account number</th>
            <th>Recipient - user code</th>  <th>Recipient - full name</th> <th>Recipient - account number</th>
        </tr>
        <c:forEach var="transfer" items="${transfer_history}">
        <tr>
            <td><fmt:formatNumber type="currency" currencySymbol="€" value="${transfer.amount}" /></td>
            <td><c:out value="${transfer.reason}" /></td>
            <td><c:out value="${transfer.issueDate}" /></td>
            <td><c:out value="${transfer.senderAccount.holderId}" /></td>
            <td><c:out value="${transfer.senderAccount.holder.firstName} ${transfer.senderAccount.holder.lastName}" /></td>
            <td><c:out value="${transfer.senderAccount.accountNumber}" /></td>
            <td><c:out value="${transfer.recipientAccount.holderId}" /></td>
            <td><c:out value="${transfer.recipientAccount.holder.firstName} ${transfer.recipientAccount.holder.lastName}" /></td>
            <td><c:out value="${transfer.recipientAccount.accountNumber}" /></td>
        </tr>
        </c:forEach>
    </table>
    </c:if>
    <c:if test="${empty transfer_history}">
    <p>No transfer stored.</p>
    </c:if>

</body>
</html>
