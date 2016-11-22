<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true"%>
<form id="comment_edit_form" method="post" 
	action="${pageContext.request.contextPath}/bbs/comment_edit_ok.do">
	<!-- 삭제 대상에 대한 상태유지 -->
	<input type="hidden" name="comment_id" value="${comment.id}" />
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" 
			aria-label="Close"><span aria-hidden="true">&times;</span>
		</button>
		<h4 class="modal-title">덧글 수정</h4>
	</div>
	<div class="modal-body">
		<!-- 자신의 글이 아닌 경우 -->
		<c:if test="${comment.memberId != loginInfo.id }">
			<div class="form-group">
				<input type="text"  name="writer_name" id="writer_name" class="form-control" 
					placeholder="작성자" value="${comment.writerName}"/>
			</div>
			<div class="form-group">
				<input type="password"  name="writer_pw" id="writer_pw" class="form-control" 
					placeholder="작성시 설정한 비밀번호"/>
			</div>
			<div class="form-group">
				<input type="email"  name="email" id="email" class="form-control" 
					placeholder="이메일" value="${comment.email}"/>
			</div>
		</c:if>
		<!-- 덧글 내용 -->
		<div class="form-group">
			<textarea class="form-control" name="content" 
				rows="5">${comment.content}</textarea>
		</div>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
		<button type="submit" class="btn btn-danger">수정</button>
	</div>
</form>