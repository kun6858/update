<%@ page language="java" contentType="text/plain; charset=EUC-KR" pageEncoding="EUC-KR" isErrorPage="true"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
{
    "errorMsg": "<c:if test="${exception.message != null}">${exception.message}</c:if><c:if test="${exception.message == null}">�˼� ���� ���� �߻�</c:if>",
    "errorClass": "${exception.stackTrace[0].className}"
}