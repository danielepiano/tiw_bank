<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <title>Login</title>
</head>
<body>

    <div style="padding: 10px; border-radius: 3px; border: solid 1px">
        <h3>LOGIN FORM</h3>
        <form action="login" method="POST">
            <label for="email">Email</label> <br>
            <input type="text" id="email" name="email"
                   value="<c:out value="${login_form.email}"/>" placeholder="Enter email" required /> <br>

            <label for="password">Password</label> <br>
            <input type="password" id="password" name="password"
                   value="<c:out value="${login_form.password}"/>" placeholder="Enter password" required /> <br><br>
            <input type="submit" value="Login">
        </form>

        <p>Don't have an account? <a href="register">Sign up!</a></p>

        <ul>
            <c:forEach var="message" items="${messages}">
                <li><c:out value="${message}" /></li>
            </c:forEach>
        </ul>
    </div>

</body>
</html>
