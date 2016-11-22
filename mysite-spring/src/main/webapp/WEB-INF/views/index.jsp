<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page trimDirectiveWhitespaces="true"%>
<!doctype html>
<html>
<head>
<%@ include file="/WEB-INF/inc/head.jsp" %>
<style type="text/css">
/** 메인페이지 > 최근게시물 > 제목 영역 */
.article-item .page-header {
	margin-bottom: 0px;
	border-bottom: 0px;
}

/** 메인페이지 > 최근게시물 > 제목 영역 > 제목 텍스트 */
.article-item h4 {
	font-weight: bold;
	margin-bottom: 0;
}

/** 메인페이지 > 최근게시물 > 제목 우측의 more 버튼 */
.article-item .btn {
	margin-bottom: -15px;
}

/** 메인페이지 > 최근게시물 > 글 목록 > 링크 */
.article-item .list-group-item a {
	display: block;
	color: #222;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

/** 케러셀 영역의 전체 높이 지정 */
#carousel {
	height: 80%;
}

/** 핸드폰 사이즈에서는 케러셀이 화면을 가득 채움 */
@media (max-width: 767px) {
	#carousel {
		height: 100%;
	}
}

/** 케러셀의 각 영역 높이가 케러셀 안에서 가득 차도록 구성 */
#carousel .item, #carousel .carousel-inner {
	height: 100%;
}

/** 케러셀 이미지 */
#carousel .img {
	height: 100%;
	background-size: cover;
	background-position: center center;
}
</style>

<script type="text/javascript">
$(function() {
	$(".carousel-inner").swipe({
	    swipeLeft: function() {
	        $(this).parent().carousel('next');
	    },
	    swipeRight: function() {
	        $(this).parent().carousel('prev');
	    },
	    threshold:0
	});
});
</script>
</head>
<body>
<%@ include file="/WEB-INF/inc/topbar.jsp" %>

<!-- 케러셀 -->
<div id="carousel" class="carousel slide" data-ride="carousel">
	<!-- Indicators -->
	<ol class="carousel-indicators">
		<c:forEach var="document" items="${galleryList}" varStatus="status">
			<c:set var="cls" value="" />
			<c:if test="${status.index == 0}">
				<c:set var="cls" value="active" />
			</c:if>
			<li data-target="#carousel" data-slide-to="${status.index}" class="${cls}"></li>
		</c:forEach>
	</ol>

	<!-- Wrapper for slides -->
	<div class="carousel-inner" role="listbox">
		<c:forEach var="document" items="${galleryList}"  varStatus="status">
			<c:set var="cls" value="" />
			<c:if test="${status.index == 0}">
				<c:set var="cls" value="active" />
			</c:if>
			
			<c:url var="image_url" value="/download.do">
				<c:param name="file" value="${document.imagePath}" />
			</c:url>
			
			<div class="item ${cls}">
				<div class="img" style="background-image:url('${image_url}')"></div>
				<div class="carousel-caption">
					<h2>${document.subject}</h2>
					<p><a href="${pageContext.request.contextPath}/bbs/document_read.do?category=gallery&document_id=${document.id}" 
					class="btn btn-primary btn-lg">view</a></p>
				</div>
			</div>
		</c:forEach>
	</div>

	<!-- Controls -->
	<a class="left carousel-control" href="#carousel" data-slide="prev">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</a>
	<a class="right carousel-control" href="#carousel" data-slide="next">
		<span class="glyphicon glyphicon-chevron-right"></span>
	</a>
</div>

<!-- 최신 게시물 목록 영역 -->
<div class="container">
	<div class="row">
		<!-- 공지사항 -->
		<div class="col-md-4 article-item">
			<div class="page-header clearfix">
				<h4 class="pull-left">공지사항</h4>
				<div class="pull-right">
					<a href="${pageContext.request.contextPath}/bbs/document_list.do?category=notice" class="btn btn-warning btn-xs">more</a>
				</div>
			</div>
			
			<ul class="list-group">
				<c:forEach var="document" items="${noticeList}">
					<li class="list-group-item">
						<a href="${pageContext.request.contextPath}/bbs/document_read.do?category=${document.category}&document_id=${document.id}">${document.subject}</a>
					</li>
				</c:forEach>
			</ul>
		</div>
		
		<!-- 자유게시판 -->
		<div class="col-md-4 article-item">
			<div class="page-header clearfix">
				<h4 class="pull-left">자유게시판</h4>
				<div class="pull-right">
					<a href="${pageContext.request.contextPath}/bbs/document_list.do?category=free" class="btn btn-warning btn-xs">more</a>
				</div>
			</div>
			
			<ul class="list-group">
				<c:forEach var="document" items="${freeList}">
					<li class="list-group-item">
						<a href="${pageContext.request.contextPath}/bbs/document_read.do?category=${document.category}&document_id=${document.id}">${document.subject}</a>
					</li>
				</c:forEach>
			</ul>
		</div>
		
		<!-- 질문답변 -->
		<div class="col-md-4 article-item">
			<div class="page-header clearfix">
				<h4 class="pull-left">질문답변</h4>
				<div class="pull-right">
					<a href="${pageContext.request.contextPath}/bbs/document_list.do?category=qna" class="btn btn-warning btn-xs">more</a>
				</div>
			</div>
			
			<ul class="list-group">
				<c:forEach var="document" items="${qnaList}">
					<li class="list-group-item">
						<a href="${pageContext.request.contextPath}/bbs/document_read.do?category=${document.category}&document_id=${document.id}">${document.subject}</a>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/inc/footer.jsp" %>
</body>
</html>