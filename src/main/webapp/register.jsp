<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <title>Login</title>
</head>
<body>

    <form action="login" method="POST">
        <label for="email">Email</label> <br>
        <input type="text" id="email" name="email"
               value="<c:out value="${login_form.email}"/>" placeholder="Inserisci email" /> <br>

        <label for="password">Password</label> <br>
        <input type="password" id="password" name="password"
               value="<c:out value="${login_form.password}"/>" placeholder="Inserisci password" /> <br><br>
        <input type="submit" value="Login">
    </form>

    <p>Non hai un account? <a href="register">Registrati</a></p>

    <ul>
        <c:forEach var="error" items="${messages}">
            <li><c:out value="${error}" /></li>
        </c:forEach>
    </ul>

</body>
</html>
