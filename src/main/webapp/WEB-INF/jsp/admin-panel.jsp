<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="lotteryDTO" type="lv.igors.lottery.lottery.dto.LotteryDTO"--%>

<%@include file="head.jsp" %>

<body>
<%@include file="navbar.jsp" %>
<div class="container">
    <div class="container-fluid">
        <%-- Cards --%>
        <div class="card" style="width: 12rem;">
            <div class="card-body">
                <button type="button" class="btn btn-primary" data-toggle="modal"
                        data-target="#new-lottery">
                    New lottery
                </button>
            </div>
        </div>

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
                            <input type="hidden" name="lotteryId" value="${lotteryDTO.id}">
                            <div class="form-group">
                                <input name="title" class="form-control" placeholder="Enter title">
                            </div>
                            <div class="form-group">
                                <input name="limit" class="form-control" placeholder="Enter limit">
                            </div>
                            <button type="submit" class="btn btn-primary">Create</button>

                        </form>
                    </div>
                </div>
            </div>
        </div>
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
                                <form method="post">
                                    <input type="hidden" name="lotteryId" value="${lotteryDTO.id}">
                                    <button type="submit" class="btn btn-danger" formaction="admin/stop-registration">
                                        Stop registration
                                    </button>
                                    <button type="submit" class="btn btn-primary" formaction="admin/choose-winner">
                                        Choose winner
                                    </button>
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