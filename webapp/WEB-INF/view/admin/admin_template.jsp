<%@ page language="java" contentType="text/html; charset=EUC-KR" pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>update.jsp 설정</title>
<meta http-equiv="Content-Type" content="text/html; charset=euc-kr" />
<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.7.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery-ui-1.8.18.custom.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/jquery-ui-timepicker-addon.js" />"></script>

<script type="text/javascript" src="<c:url value="/resources/js/jquery.form.js" />"></script>

<script type="text/javascript" src="<c:url value="/resources/js/common.js" />"></script>

<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/admin.css" />" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/jquery-ui-1.8.18.custom.css" />" />
</head>
<body>
<div id="wrapper">
    <h1>update.jsp 설정</h1>
    <ul id="nav">
        <li class="a"><a href="<c:url value="/admin/server_manage.byto"/>">서버관리</a></li>
        <li class="d"><a href="<c:url value="/admin/package_manage.byto"/>">패키지관리</a></li>
        <li class="c"><a href="<c:url value="/admin/file_manage.byto"/>">파일관리</a></li>
        <li class="b"><a href="<c:url value="/admin/logout.byto"/>">로그아웃</a></li>
    </ul>
    <div id="bodyWrapper">
		<div id="body">
		<decorator:body></decorator:body>
		</div><!-- end body -->
    </div><!-- end bodyWrapper -->
    
    <div id="footer">
		Powered by <a href="http://www.byto.com">Byto</a>
    </div><!-- end footer -->
</div><!-- end wrapper -->

<div id="loading">
	<img src="<c:url value="/resources/images/loading.gif"/>" />
</div>
</body>
</html>