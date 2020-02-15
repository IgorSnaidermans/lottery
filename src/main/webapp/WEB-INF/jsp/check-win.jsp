<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="statusResponse" type="lv.igors.lottery.statusResponse.StatusResponse"--%>
<%--@elvariable id="bindingResult" type="org.springframework.validation.BindingResult"--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>

<div class="card" style="width: 30rem;">
    <div class="card-body">
        <p>${statusResponse.status}</p>
        <a href="/" class="btn btn-primary">
            Back
        </a>
    </div>
</div>
</body>