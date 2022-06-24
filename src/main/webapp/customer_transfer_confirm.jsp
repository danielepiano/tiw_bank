<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
    <title>Transfer confirmation</title>
    <style>
        table { font-family: arial, sans-serif; border-collapse: collapse;}
        td, th { border: 1px solid #dddddd; text-align: left; padding: 8px;}
        tr:nth-child(even) { background-color: #dddddd;}
    </style>
</head>
<body>

    <p><a href="logout">logout</a></p>
    <p>← Back to <a href="manage-current-account?id=<c:out value="${picked_currentAccount.id}" />">current account management</a></p>

    <p>Successful transfer!</p>

    <h1>Transfer effected</h1>
    <table>
        <tr>
            <th>Amount</th> <th>Reason</th> <th>Issue date</th>
            <th>Sender - user code</th>     <th>Sender - full name</th>    <th>Sender - account number</th>
            <th>Recipient - user code</th>  <th>Recipient - full name</th> <th>Recipient - account number</th>
        </tr>
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
    </table> <br>

    <h3>Previous balance: <fmt:formatNumber type="currency" currencySymbol="€" value="${picked_currentAccount.balance + transfer.amount}" /> </h3>
    <h3>Current balance: <fmt:formatNumber type="currency" currencySymbol="€" value="${picked_currentAccount.balance}" /> </h3>

</body>
</html>
