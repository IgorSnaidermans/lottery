<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="lotteryTimeFormattedDTO" type="lv.igors.lottery.lottery.dto.LotteryTimeFormattedDTO"--%>

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
                            <input type="hidden" name="lotteryId" value="${lotteryTimeFormattedDTO.id}">
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
            <c:forEach items="${lotteries}" var="lotteryTimeFormattedDTO">
                <%-- Cards --%>
                <div class="card" style="width: 18rem;">
                    <div class="card-body">
                        <h5 class="card-title">${lotteryTimeFormattedDTO.title}</h5>
                        <p class="card-text">${lotteryTimeFormattedDTO.startTimestampFormatted}</p>
                        <button type="button" class="btn btn-primary" data-toggle="modal"
                                data-target="#lottery${lotteryTimeFormattedDTO.id}">
                            Open
                        </button>
                    </div>
                </div>

                <!-- Modal -->
                <div class="modal fade" id="lottery${lotteryTimeFormattedDTO.id}" tabindex="-1" role="dialog"
                     aria-hidden="true">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="exampleModalLabel">${lotteryTimeFormattedDTO.title}</h5>
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                            <div class="modal-body">
                                <form method="post">

                                    <p class="card-text">
                                        Id: ${lotteryTimeFormattedDTO.id}
                                        <br/>
                                        Active: ${lotteryTimeFormattedDTO.active}
                                        <br/>
                                        Participants: ${lotteryTimeFormattedDTO.participants}
                                        <br/>
                                        Limit: ${lotteryTimeFormattedDTO.participantsLimit}
                                        <br/>
                                        Winner code: <c:if test="${!lotteryTimeFormattedDTO.winnerCode}">
                                        ${lotteryTimeFormattedDTO.winnerCode}<br/>
                                        Winner email: ${lotteryTimeFormattedDTO.winnerEmail}</c:if>
                                        <c:if test="${lotteryTimeFormattedDTO.winnerCode}">No</c:if>
                                        <br/>
                                        Started: ${lotteryTimeFormattedDTO.startTimestampFormatted}
                                        <br/>Ended: <c:if
                                            test="${lotteryTimeFormattedDTO.endTimestampFormatted==null}">No</c:if>
                                        <c:if test="${lotteryTimeFormattedDTO.endTimestampFormatted!=null}">
                                            ${lotteryTimeFormattedDTO.endTimestampFormatted}
                                        </c:if>
                                    </p>

                                    <input type="hidden" name="lotteryId" value="${lotteryTimeFormattedDTO.id}">
                                    <c:if test="${lotteryTimeFormattedDTO.active}">
                                        <button type="submit" class="btn btn-danger"
                                                formaction="admin/stop-registration">
                                            Stop registration
                                        </button>
                                    </c:if>

                                    <c:if test="${lotteryTimeFormattedDTO.winnerCode==null}">
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