<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<script type="text/javascript" src="<c:url value="/resources/js/jquery-1.7.1.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/resources/js/common.js" />"></script>
<title>로그인페이지</title>
<style>
	#loginPanel {
		position: absolute;
		top: 40%;
		left: 40%;  
	    border: 2px solid brown;
	}
</style>
<script>
	$(document).ready(function() {
		$("input[name='id']").focus();
		
		$("#form").submit(function(event){
			event.preventDefault();
			var id = $("input[name='id']").val();
			var passwd = $("input[name='passwd']").val();
			var url = $(this).attr("action");
			var type = $(this).attr("method");
			
			$.ajax({
				type : type,
				url : url,
				data : {
					id : id,
					passwd : passwd
				},
				success : function() {
					location.href = "<c:url value='/admin/server_manage.byto'/>";
				},
				error: ajaxErrorHandler
			});
		});
	});
</script>
</head>
<body>
	<form id="form" action="<c:url value='/ajax/login.byto'/>" method="POST">
	<div id="loginPanel">
		<table>
			<tr>
				<td>아이디 :</td>
				<td><input type="text" name="id" /></td>					
			</tr>
			<tr>
				<td>비밀번호 :</td>
				<td><input type="password" name="passwd" /></td>					
			</tr>
			<tr>
				<td colspan="2" style="text-align:center;">
					<input type="submit" value="로그인" />
				</td>
			</tr>
		</table>
	</div>
	</form>
</body>
</html>