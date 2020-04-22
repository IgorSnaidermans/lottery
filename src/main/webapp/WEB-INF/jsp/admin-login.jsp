<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>

<form:form action="/login" modelAttribute="user">
    <div><label> Username: <form:input type="text" path="username"/> </label></div>
    <div><label> Password: <form:input type="password" path="password"/> </label></div>

    <c:if test="${param.error}"><p>Wrong username or password</p></c:if>
    <c:if test="${param.logout!=null}"><p>You have been logged out</p></c:if>
    <p>To get password - contact website owner</p>
    <div><input type="submit" value="Sign In"/></div>
</form:form>
</body>
