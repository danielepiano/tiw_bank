<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<head>
    <title>Home</title>
    <style>
        table { font-family: arial, sans-serif; border-collapse: collapse;}
        td, th { border: 1px solid #dddddd; text-align: left; padding: 8px;}
        tr:nth-child(even) { background-color: #dddddd;}
    </style>
</head>
<body>

    <p><a href="logout">logout</a></p>

    <h1>Welcome back, <c:out value="${user.firstName} ${user.lastName}" />!</h1>

    <c:if test="${not empty myCurrentAccounts}">
    <table >
        <tr>
            <th>Account number</th>
            <th>Balance</th>
        </tr>
        <c:forEach var="ca" items="${myCurrentAccounts}">
        <tr>
            <td><c:out value="${ca.accountNumber}" /></td>
            <td><fmt:formatNumber type="currency" currencySymbol="€" value="${ca.balance}" /></td>
            <td><a href="manage-current-account?id=<c:out value="${ca.id}" />">Manage current account</a></td>
        </tr>
        </c:forEach>
    </table>
    </c:if>
    <c:if test="${empty myCurrentAccounts}">
    <p>No current account opened. Talk to an administrator and open one!</p>
    </c:if>

</body>
</html>
