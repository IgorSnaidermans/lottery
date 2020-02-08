<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="lotteryDTO" type="lv.igors.lottery.lottery.dto.LotteryDTO"--%>
<%--@elvariable id="statusResponse" type="lv.igors.lottery.statusResponse.StatusResponse"--%>
<%--@elvariable id="response" type="lv.igors.lottery.statusResponse.Responses"--%>
<%--@elvariable id="bindingResult" type="org.springframework.validation.BindingResult"--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>

<section>
    <div class="container">
        <c:if test="${statusResponse.status.equals('Fail')}">
            <c:out value="<p>An error happened: ${statusResponse.reason}</p>"/> </c:if>


        ${statusResponse.status.contains("")}
        ${welcome}
        <c:out value='${welcome}'/>


        <div class="container-fluid">
            <div class="row">
                <c:forEach items="${lotteries}" var="lotteryDTO">

                    <%-- Cards --%>
                    <div class="card" style="width: 18rem;">
                        <div class="card-body">
                            <h5 class="card-title">${lotteryDTO.title}</h5>
                            <p class="card-text">${lotteryDTO.startTimestamp}</p>
                            <button type="button" class="btn btn-primary" data-toggle="modal"
                                    data-target="#lottery${lotteryDTO.id}">
                                Open
                            </button>
                        </div>
                    </div>

                    <!-- Modal -->
                    <div class="modal fade" id="lottery${lotteryDTO.id}" tabindex="-1" role="dialog" aria-hidden="true">
                        <div class="modal-dialog" role="document">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <h5 class="modal-title" id="exampleModalLabel">${lotteryDTO.title}</h5>
                                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                        <span aria-hidden="true">&times;</span>
                                    </button>
                                </div>
                                <div class="modal-body">
                                    <form action="/register">
                                        <input type="hidden" name="lotteryId" value="${lotteryDTO.id}">
                                        <div class="form-group">
                                            <input name="email" type="email" class="form-control"
                                                   aria-describedby="emailHelp" placeholder="Enter email">
                                        </div>
                                        <div class="form-group">
                                            <input name="code" class="form-control" placeholder="Enter code">
                                        </div>
                                        <div class="form-group">
                                            <input name="age" class="form-control" placeholder="Your age">
                                        </div>
                                        <button type="submit" class="btn btn-primary" formmethod="post">Register
                                        </button>
                                        <button type="submit" class="btn btn-primary" formmethod="get"
                                                formaction="/status">Check win
                                        </button>
                                    </form>
                                    <p class="text-center">By submitting the data, you agree that the website owner will
                                        store them
                                        & it will be used for educational purposes.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</section>
</body>