<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page trimDirectiveWhitespaces="true"%>
<!doctype html>
<html>
<head>
<%@ include file="/WEB-INF/inc/head.jsp" %>
<style type="text/css">
	/* 게시물 항목 하나 */
	.item { padding: 0px 5px; }
	/* 게시물 제목 */
	.item h4 { margin-top: 10px; margin-bottom: 5px; }
	/* 썸네일 이미지 크기 */
	.img-responsive { width: 100%; }
</style>
</head>
<body>
<%@ include file="/WEB-INF/inc/topbar.jsp" %>
<div class="container">
	<h1 class="page-header">${bbsName} - <small>글 목록</small></h1>
		
	<!-- 글 목록 시작 -->
	<div class="row multi-columns-row">
		<!-- 조회된 글이 있는 경우 시작 -->
		<c:choose>
			<c:when test="${fn:length(documentList) > 0}">
				<c:forEach var="document" items="${documentList}">
					<!-- 게시물 항목 하나 -->
					<div class="col-lg-3 col-md-3 col-sm-6 col-xs-12">
						<div class="thumbnail">
							<c:url var="readUrl" value="/bbs/document_read.do">
								<c:param name="category" value="${document.category}" />
								<c:param name="document_id" value="${document.id}" />
							</c:url>
							<!-- 링크 + 썸네일 -->
							<a href="${readUrl}">
								<c:choose>
									<c:when test="${document.imagePath != null}">
										<c:url var="downloadUrl" value="/download.do">
											<c:param name="file" value="${document.imagePath}" />
										</c:url>
										<img src="${downloadUrl}" class="img-responsive"/>
									</c:when>
									<c:otherwise>
										<img src="${pageContext.request.contextPath}/assets/img/no_image.jpg" class="img-responsive"/>
									</c:otherwise>
								</c:choose>
							</a>
							<!-- 제목 + 작성자 + 조회수 -->
							<div class="item">
								<h4>${document.subject}</h4>
								<div>${document.writerName}</div>
								<div class="clearfix">
									<div class="pull-left">${document.regDate}</div>
									<div class="pull-right">${document.hit} view</div>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="col-md-12">
					<p class="text-center">조회된 글이 없습니다.</p>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
	<!--// 글 목록 끝 -->

	<!-- 하단 공통 영역 처리 -->
	<%@ include file="/WEB-INF/inc/bbs_list_bottom.jsp" %>
</div>
<%@ include file="/WEB-INF/inc/footer.jsp" %>
</body>
</html>