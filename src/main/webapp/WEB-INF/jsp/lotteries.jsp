<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@include file="head.jsp" %>


<body>
<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="lottery" type="lv.igors.lottery.lottery.LotteryDTO"--%>

<c:if test="${lotteries.isEmpty()}">
    <p>No lotteries for today, sorry.</p>
</c:if>


<c:if test="${!lotteries.isEmpty()}">
    <c:forEach items="${lotteries}" var="lottery">
        ${lottery.title}
    </c:forEach>

</c:if>
</body>

</html>