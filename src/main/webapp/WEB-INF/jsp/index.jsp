<!DOCTYPE html>
<html lang="en">

<%--@elvariable id="lotteries" type="java.util.List"--%>
<%--@elvariable id="lottery" type="lv.igors.lottery.lottery.LotteryDTO"--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@include file="head.jsp" %>

<body id="page-top">

<!-- Navigation -->
<nav class="navbar navbar-expand-lg bg-secondary text-uppercase" id="mainNav">
    <div class="container">
        <a class="navbar-brand js-scroll-trigger" href="#page-top">HelloIT Bootcamp</a>
        <button class="navbar-toggler navbar-toggler-right text-uppercase font-weight-bold bg-primary text-white rounded"
                type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive"
                aria-expanded="false" aria-label="Toggle navigation">Menu
        </button>
        <div class="collapse navbar-collapse" id="navbarResponsive">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item mx-0 mx-lg-1">
                    <a class="nav-link py-3 px-0 px-lg-3 rounded js-scroll-trigger" href="#lotteries">Lotteries</a>
                </li>
                <%--todo add admin menu section--%>
            </ul>
        </div>
    </div>
</nav>

<!-- Masthead -->
<header class="bg-primary text-white text-center">
    <p class="masthead-subheading font-weight-light mb-0">Note: the website is made for educational use only</p>
</header>

<!-- Lottery Section -->
<section class="page-section portfolio" id="lotteries">
    <div class="container">

        <h2 class="page-section-heading text-center text-uppercase text-secondary mb-0">Lotteries</h2>
        <div class="divider-custom">
            <div class="divider-custom-line"></div>
        </div>

        <!-- Portfolio Grid Items -->
        <c:forEach items="${lotteries}" var="lottery">
        <div class="col-md-6 col-lg-4">
            <div class="portfolio-item mx-auto" data-toggle="modal" data-target="#portfolioModal1">
                <div class="portfolio-item-caption d-flex align-items-center justify-content-center h-100 w-100">
                    <div class="portfolio-item-caption-content text-center text-white">
                        <i class="fas fa-plus fa-3x"></i>
                    </div>
                </div>
                <img class="img-fluid"
                     src="https://images.footballfanatics.com/FFImage/thumb.aspx?i=/productimages/_3627000/ff_3627532-3bab8018518db7210db4_full.jpg&w=900"
                     alt="">
            </div>
        </div>


            <%--MODAL START--%>
        <div class="portfolio-modal modal fade" id="portfolioModal1" tabindex="-1" role="dialog"
             aria-labelledby="portfolioModal1Label" aria-hidden="true">
            <div class="modal-dialog modal-xl" role="document">
                <div class="modal-content">

                    <div class="modal-body text-center">
                        <div class="container">
                            <div class="row justify-content-center">
                                <div class="col-lg-8">
                                    <!-- Portfolio Modal - Title -->
                                    <h2 class="portfolio-modal-title text-secondary text-uppercase mb-0">
                                        Lottery</h2>
                                    <!-- Icon Divider -->
                                    <div class="divider-custom">
                                        <div class="divider-custom-line"></div>
                                    </div>
                                    <!-- Portfolio Modal - Image -->
                                    <img class="img-fluid rounded mb-5" src="" alt="">
                                    <!-- Form -->
                                    <form name="sentMessage" id="contactForm" novalidate="novalidate">
                                        <input class="form-control" id="code" type="text" placeholder="Code"
                                               required="required"
                                               data-validation-required-message="<%--todo validation message--%>">
                                        <input class="form-control" id="email" type="email"
                                               placeholder="Email Address" required="required"
                                               data-validation-required-message="<%--todo validation message--%>">
                                        <p>By sending the data, you agree that it will be stored by the
                                            website owner and used for lottery and educational purposes</p>
                                        <button type="submit" class="btn btn-primary"
                                                id="sendMessageButton">Register
                                        </button>
                                    </form>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            </c:forEach>
</section>

<!-- Copyright Section -->
<section class="copyright py-4 text-center text-white">
    <div class="container">
        <small>FOR EDUCATIONAL USE ONLY &copy; Igors Shnaidermans 2020</small>
    </div>
</section>
<%--todo import this from other file--%>

<!-- Scroll to Top Button (Only visible on small and extra-small screen sizes) -->
<div class="scroll-to-top d-lg-none position-fixed ">
    <a class="js-scroll-trigger d-block text-center text-white rounded" href="#page-top">
        <i class="fa fa-chevron-up"></i>
    </a>
</div>

<!-- Bootstrap core JavaScript -->
<script src="https://code.jquery.com/jquery-3.4.1.min.js"
        integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"
        integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"
        integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM"
        crossorigin="anonymous"></script>
</body>

</html>
