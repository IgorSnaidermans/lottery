<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="statusResponse" type="lv.igors.lottery.statusResponse.StatusResponse"--%>
<%--@elvariable id="bindingResult" type="org.springframework.validation.BindingResult"--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>

<div class="card" style="width: 20rem;">
    <div class="card-body">
        <p>An error happened: ${statusResponse.reason}</p>
        ${bindingResult.globalError.toString()}
        <a href="/${back}" class="btn btn-primary">
            Back
        </a>
    </div>
</div>
</body>