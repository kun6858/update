<?xml version="1.0" encoding="euc-kr" ?>
<%@ page contentType="text/xml; charset=euc-kr" %>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page import="com.byto.util.BytoUtils" %>
<%@ page import="com.byto.vo.Server" %>
<%@ page import="com.byto.vo.File" %>
<update_info app_name="${package.appName}" version="${package.version}" count="${count}">
	<c:forEach var="fileList" varStatus="varStatus" items="${file}" step="1">
		<${fileList.tagName} version="${fileList.version}" type="${fileList.type}" reg="${fileList.reg}" size="${fileList.size}">
		<remote>http://${server.host}<c:if test="${server.port != '80'}">:${server.port}</c:if>${fileList.downPath}</remote>
		<local>${fileList.localFileName}</local>
		</${fileList.tagName}>
	</c:forEach>
</update_info>