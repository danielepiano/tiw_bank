<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <title>Registrati</title>
</head>
<body>

    <p>← Back to <a href="login">login</a></p>

    <form action="register" method="POST">
        <label for="first_name">First name</label> <br>
        <input type="text" id="first_name" name="first_name"
               value="<c:out value="${reg_form.firstName}"/>" placeholder="Enter first name" /> <br>

        <label for="last_name">Last name</label> <br>
        <input type="text" id="last_name" name="last_name"
               value="<c:out value="${reg_form.lastName}"/>" placeholder="Enter last name" /> <br>

        <label for="email">Email</label> <br>
        <input type="text" id="email" name="email"
               value="<c:out value="${reg_form.email}"/>" placeholder="Enter email" /> <br>

        <label for="password">Password</label> <br>
        <input type="password" id="password" name="password"
               value="<c:out value="${reg_form.password}"/>" placeholder="Enter password" /> <br>

        <label for="confirm_password">Password</label> <br>
        <input type="password" id="confirm_password" name="confirm_password"
               value="<c:out value="${reg_form.confirmPassword}"/>" placeholder="Confirm password" /> <br><br>
        <input type="submit" value="Sign up">
    </form>

    <ul>
        <c:forEach var="error" items="${messages}">
            <li><c:out value="${error}" /></li>
        </c:forEach>
    </ul>

</body>
</html>
