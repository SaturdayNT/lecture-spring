<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<!doctype html>
<html>
<head>
<%@ include file="/WEB-INF/inc/head.jsp"%>
<style type="text/css">
/** hr 태그에 대한 상하 여백 확보 */
.featurette-divider {
    margin: 80px 0;
}

/** 제목 텍스트 모양 */
.featurette-heading {
    font-weight: bold; line-height: 1; letter-spacing: -1px;
}

/** 상세 내용영역의 반응형 기능 >> 제목의 글자 크기 재설정 */
@media (min-width: 768px) {
    .featurette-heading {
        font-size: 50px;
    }
}

@media (min-width: 992px) {
    .featurette-heading {
        margin-top: 120px;
    }
}
</style>
</head>
<body>
<%@ include file="/WEB-INF/inc/topbar.jsp"%>
<!-- 내용영역 -->
<div class="container">

    <h1 class="page-header">Twitter Bootstrap 소개</h1>

    <!-- 그리드 시스템 (데스크탑 가로3칸) -->
    <div class="row">
        <div class="col-md-4 text-center">
            <img src="${pageContext.request.contextPath}/assets//img/html5.jpg" class="img-circle" width="240" height="240"/>
            <h2>HTML5</h2>
            <p>
                웹 접근성과 시멘틱 웹, 그리고 XTHML1.0과 HTML5의 차이점에 대한 이해는 반응형 웹을 시작하는 첫 단계 입니다. 그렇기 때문에 이 단원에서는 가장 우선적으로 HTML5의 변경된
                웹 페이지 구성 방법과, 새롭게 추가된 시멘틱 요소들 및 멀티미디어 제어 요소들을 소개하고 기본적인 활용 과정을 소개합니다.
            </p>
        </div>
        <div class="hidden-lg hidden-md">
            <br/> <br/>
        </div>
        <div class="col-md-4 text-center">
            <img src="${pageContext.request.contextPath}/assets//img/css3.jpg" class="img-circle" width="240" height="240"/>
            <h2>CSS3</h2>
            <p>
                기존에 사용되던 CSS2와 함께 사용할 수 있는 다채로운 그래픽 효과에 대해서 소개합니다. CSS3의 그림자,그라데이션 효과는 이미지 제작 없이 상당수의 웹 페이지들을 CSS만으로 제작
                가능하게 해 주었습니다. CSS3는 CSS2에 효과들이 추가되는 개념이기 때문에 웹 표준 기술(XHTML1.0+CSS2)에 대한 이해가 필요합니다.
            </p>
        </div>
        <div class="hidden-lg hidden-md">
            <br/> <br/>
        </div>
        <div class="col-md-4 text-center">
            <img src="${pageContext.request.contextPath}/assets//img/bs3.jpg" class="img-circle" width="240" height="240"/>
            <h2>Bootstrap3</h2>
            <p>
                Bootstrap은 트위터에서 만든 반응형 웹 Framework로, 어려운 CSS3나 Javascript에 대한 이해가 없이도 반응형 웹을 손쉽게 만들어 줄 수 있는 도구입니다.
                기본적으로 제공하는 페이지의 형태 위에 사용자가 직접 제작한 CSS를 추가하면 다채로운 반응형 웹 페이지를 제작할 수 있습니다.
            </p>
        </div>
    </div>
    <!--// 그리드 시스템 -->

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">
                첫 번째 예제를 확인하세요. <span class="text-muted">마음에 드실겁니다.</span>
            </h2>
            <p class="lead">
                가장 심플한 것이 가장 화려한 것이라는 말이 있습니다. 첫 번째 예제를 통해서 심플하면서도 모던한 웹 퍼블리싱을 경험해 보신다면, Bootstrap3의 매력에 반하시게 될 것입니다.
            </p>
        </div>
        <div class="col-md-5">
            <img class="img-thumbnail img-responsive" src="${pageContext.request.contextPath}/assets//img/img01.jpg" width="500" height="500"/>
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-5">
            <img class="img-thumbnail img-responsive" src="${pageContext.request.contextPath}/assets//img/img02.jpg" width="500" height="500"/>
        </div>
        <div class="col-md-7">
            <h2 class="featurette-heading">무척 쉽습니다. <span class="text-muted">직접 경험해 보세요.</span></h2>
            <p class="lead">
                Twitter Bootstrap3를 사용하면 반응형 웹 페이지 제작이 매우 쉬워집니다. 빠르게 메뉴를 제작하고, 어려운 자바스크립트의 사용 없이도 다이나믹한 컨텐츠의 제공이 가능해 집니다. 단지 HTML 태그의 사용 방법만 알고 계시면 됩니다. CSS의 활용까지 가능하다면, 당신의 가능성은 무한대 입니다.
            </p>
        </div>
    </div>

    <hr class="featurette-divider">

    <div class="row featurette">
        <div class="col-md-7">
            <h2 class="featurette-heading">단순함이 모여서 화려함이 됩니다. <span class="text-muted">정말 멋집니다.</span></h2>
            <p class="lead">
                지금 경험하시고 계신 페이지는 Bootstrap의 가장 대표적인 기능들을 모두 모아놓은 페이지에 약간의 CSS 기능을 더한 것 입니다. 단순한 기능들이 모인 것 이지만, 결과물은 매우 화려합니다.
            </p>
        </div>
        <div class="col-md-5">
            <img class="img-thumbnail img-responsive" src="${pageContext.request.contextPath}/assets//img/img03.jpg" width="500" height="500"/>
        </div>
    </div>
</div>
<!--// 내용영역 -->
<%@ include file="/WEB-INF/inc/footer.jsp"%>
</body>
</html>