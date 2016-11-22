<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true"%>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no"/>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>My JSP Page</title>
<!-- Twitter Bootstrap3 & jQuery -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css" />
<script src="http://code.jquery.com/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
	
<!-- 공통 CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/common.css" />

<!-- 다음 우편번호 검색 스크립트 -->
<script src="http://dmaps.daum.net/map_js_init/postcode.v2.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/daumPostCode.js"></script>

<!-- CKEditor -->
<script src="http://cdn.ckeditor.com/4.4.7/standard/ckeditor.js"></script>

<!-- Multi-column -->
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/plugins/multi-column/ie-row-fix.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/multi-column/multi-columns-row.css" />

<!-- handlebars -->
<script src="${pageContext.request.contextPath}/assets/plugins/handlebars/handlebars-v4.0.5.js"></script>

<!-- ajax -->
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/plugins/ajax/ajax_helper.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/plugins/ajax/ajax_helper.js"></script>

<!-- ajaxForm -->
<script type="text/javascript" src="${pageContext.request.contextPath}/assets/plugins/ajax-form/jquery.form.min.js"></script>