<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="statisticsDTO" type="lv.igors.lottery.lottery.dto.StatisticsDTO--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>
<div class="container">
    <div class="container-fluid">
        <div class="row">
            <c:forEach items="${lotteries}" var="statisticsDTO">
                <%-- Cards --%>
                <div class="card" style="width: 18rem;">
                    <div class="card-body">
                        <h5 class="card-title">${statisticsDTO.title}</h5>
                        <p class="card-text">Started: ${statisticsDTO.startTimestamp}
                            <br/>Ended: <c:if test="${statisticsDTO.endTimestamp==null}">No</c:if>
                            <c:if test="${statisticsDTO.endTimestamp!=null}">${statisticsDTO.endTimestamp}</c:if>
                            <br/>Participants: ${statisticsDTO.participants}</p>
                    </div>
                </div>

            </c:forEach>
        </div>
    </div>
</div>
</body>


