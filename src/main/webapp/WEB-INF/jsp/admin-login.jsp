<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="statusResponse" type="lv.igors.lottery.statusResponse.StatusResponse"--%>
<%--@elvariable id="bindingResult" type="org.springframework.validation.BindingResult"--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>

<form action="/admin-login" method="post">
    <div><label> Username: <input type="text" name="username"/> </label></div>
    <div><label> Password: <input type="password" name="password"/> </label></div>
    <c:if test="${error}"><p>Wrong username or password</p></c:if>
    <p>To get password - contact website owner</p>
    <div><input type="submit" value="Sign In"/></div>
</form>
</body>