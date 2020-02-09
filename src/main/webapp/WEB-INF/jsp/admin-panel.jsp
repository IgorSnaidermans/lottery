<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="lotteryAdminDTO" type="lv.igors.lottery.lottery.dto.LotteryAdminDTO"--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>
<div class="container">
    <div class="container-fluid">
        <%-- Cards --%>
        <button type="button" class="btn btn-primary" data-toggle="modal"
                data-target="#new-lottery">
            New lottery
        </button>


        <!-- Modal -->
        <div class="modal fade" id="new-lottery" tabindex="-1" role="dialog" aria-hidden="true">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="exampleModalLabel">New Lottery</h5>
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                    </div>
                    <div class="modal-body">
                        <form action="/admin/start-registration" method="post">
                            <input type="hidden" name="lotteryId" value="${lotteryAdminDTO.id}">
                            <div class="form-group">
                                <input required="required" name="title" class="form-control" placeholder="Enter title">
                            </div>
                            <div class="form-group">
                                <input required="required" name="limit" class="form-control" placeholder="Enter limit">
                            </div>
                            <button type="submit" class="btn btn-primary">Create</button>

                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <c:forEach items="${lotteries}" var="lotteryAdminDTO">
                <%-- Cards --%>
                <div class="card" style="width: 18rem;">
                    <div class="card-body">
                        <h5 class="card-title">${lotteryAdminDTO.title}</h5>
                        <p class="card-text">${lotteryAdminDTO.startTimestampFormatted}</p>
                        <button type="button" class="btn btn-primary" data-toggle="modal"
                                data-target="#lottery${lotteryAdminDTO.id}">
                            Open
                        </button>
                    </div>
                </div>

                <!-- Modal -->
                <div class="modal fade" id="lottery${lotteryAdminDTO.id}" tabindex="-1" role="dialog"
                     aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="exampleModalLabel">${lotteryAdminDTO.title}</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <form method="post">

                                    <p class="card-text">
                                        Id: ${lotteryAdminDTO.id}
                                        <br/>
                                        Active: ${lotteryAdminDTO.active}
                                        <br/>
                                        Participants: ${lotteryAdminDTO.participants}
                                        <br/>
                                        Limit: ${lotteryAdminDTO.participantsLimit}
                                        <br/>
                                        Winner code: <c:if test="${!lotteryAdminDTO.winnerCode}">
                                        ${lotteryAdminDTO.winnerCode}<br/>
                                        Winner email: ${lotteryAdminDTO.winnerEmail}</c:if>
                                        <br/>
                                        Started: ${lotteryAdminDTO.startTimestampFormatted}
                                        <br/>Ended: <c:if
                                            test="${lotteryAdminDTO.endTimestampFormatted==null}">No</c:if>
                                        <c:if test="${lotteryAdminDTO.endTimestampFormatted!=null}">
                                            ${lotteryAdminDTO.endTimestampFormatted}
                                        </c:if>
                                    </p>

                                    <input type="hidden" name="lotteryId" value="${lotteryAdminDTO.id}">
                                    <c:if test="${lotteryAdminDTO.active}">
                                        <button type="submit" class="btn btn-danger"
                                                formaction="admin/stop-registration">
                                            Stop registration
                                        </button>
                                    </c:if>

                                    <c:if test="${lotteryAdminDTO.winnerCode==null}">
                                        <button type="submit" class="btn btn-primary" formaction="admin/choose-winner">
                                            Choose winner
                                        </button>
                                    </c:if>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
</div>
</body>