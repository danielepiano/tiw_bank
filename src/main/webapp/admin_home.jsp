<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <title>ADMIN HOME</title>
</head>
<body>

    <p><a href="logout">logout</a></p>

    <h1>Welcome back, <c:out value="${user.firstName} ${user.lastName}" />!</h1>

    <div style="padding: 10px; border-radius: 3px; border: solid 1px">
        <h3>CURRENT ACCOUNT CREATION</h3>
        <form action="create-current-account" method="POST">
            <label for="opening_balance">Opening balance</label> <br>
            <input type="number" id="opening_balance" name="opening_balance"
                   min="0" step="0.01"
                   value="<c:out value="${ca_form.openingBalance}"/>" placeholder="Enter opening balance" required /> <br>

            <label for="holder_id">Holder</label> <br>
            <select id="holder_id" name="holder_id" required>
                <c:forEach var="holder" items="${holders}">
                    <option value="<c:out value="${holder.id}" />">
                        <c:out value="${holder.firstName} ${holder.lastName}" />
                    </option>
                </c:forEach>
            </select> <br><br>

            <input type="submit" value="Create current account">
        </form>

        <ul>
            <c:forEach var="message" items="${messages}">
                <li><c:out value="${message}" /></li>
            </c:forEach>
        </ul>
    </div>

</body>
</html>
