<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<script type="text/javascript">
$(document).ready(function() {
	init(); //초기화
});

function init() {
	loading();
	$(':button[id="register"]').click(addServer); //서버 등록
	$("select[name='newState']").change(syncColor);
}

function loading(){
	tdHTML = "";
	
	$.ajax({
		url : "<c:url value='/ajax/list_server.byto'/>",
		dataType : 'json',
		data : {
		},
		success : makeHTML,
		error : ajaxErrorHandler
	});
}

function makeHTML(data) {
	HTML = "";
	$.each(data, function(key, val){
		var seq = data[key].seq;
		var host = data[key].host;
		var port = data[key].port;
		var state = data[key].state;
		var regDate = data[key].regDate;
		var stateMsg = (state == 1) ? "<span class='stateOk'>정상</span>" : "<span class='stateHalt'>중지</span>";
		var stateClass = (state == 1) ? "stateOk" : "stateHalt";
		
		HTML += "<tr>";
		HTML += "<td>" + parseInt(key+1) + "</td>";
		HTML += "<td><input type='text' id='host_" + seq + "' value='" + host + "'/></td>";
		HTML += "<td><input type='text' id='port_" + seq + "' value='" + port + "'/></td>";
		HTML += "<td class='" + stateClass + "'>" + stateMsg + "</td>";
		HTML +=	"<td>" + regDate + "</td>";
		HTML +=	"<td>";
		HTML +=	"<button name='modify' value='" + seq + "'>적용</button>";
		HTML += "<button name='state' state='"+state+"' value='" + seq + "'>" + stateMsg + "</button>";
		HTML += "<button name='delete' value='" + seq + "'>삭제</button>";
		HTML += "</td>";
		HTML += "</tr>";
	});
	
	$("tbody").html(HTML);
	
	$(':button[name="state"]').click(modServerState); //서버 상태 변경
	$(':button[name="modify"]').click(modServer); //서버 시퀀스 or 호스트 or 포트 변경
	$(':button[name="delete"]').click(delServer); //서버 삭제
}

function addServer(event) {
	var host =  $('input[name="newHost"]').val();
	var port =  $('input[name="newPort"]').val();
	var state = $('select[name="newState"]').val();
	
	if(!host) { alert("서버 HOST는 필수항목입니다."); return false; }
	if(!port) { alert("서버 PORT는 필수항목입니다."); return false; }
	if(!state) { alert("서버 STATE는 필수항목입니다."); return false; }
	
	if(!confirm("등록하시겠습니까?")) return false;
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/add_server.byto'/>",
		data : {
			host : host,
			port : port,
			state : state
		},
		success : function() {
			loading();
			$('input[name="newHost"]').val("");
			$('input[name="newPort"]').val("");
			$('select[name="newState"] > option[value="1"]').attr("selected", "true");
		},
		error: ajaxErrorHandler
	});
}

function delServer(event) {
	if(!confirm("서버를 삭제하시겠습니까?")) return false;
	
	var seq = $(this).val();
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/del_server.byto'/>",
		data : 
		{
			seq : seq
		},
		success : loading,
		error: ajaxErrorHandler
	});
}

function modServer(event) {
	var seq = $(this).val();
	var host = $('#host_' + seq).val();
	var port = $('#port_' + seq).val();
	
	if(!host) { alert("서버 HOST는 필수항목입니다."); return false; }
	if(!port) { alert("서버 PORT는 필수항목입니다."); return false; }

	if(!confirm("서버설정을 변경하시겠습니까?")) return false;
	
	$.ajax({
		type : "POST",
		url : "<c:url value='/ajax/mod_server.byto'/>",
		dataType : "json",
		data : 
		{
			seq : seq,
			host : host,
			port : port
		},
		success : function() {
			alert("서버정보가 성공적으로 변경되었습니다.");
		},
		error : ajaxErrorHandler
	});
}

function modServerState(event) {
	var seq = $(this).val();
	var state = $(this).attr("state");
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/mod_server_state.byto'/>",
		data : {
			seq : seq,
			state : state
		},
		success : loading,
		error : ajaxErrorHandler
	});
}
</script>
<table id="information">
	<col width="80" />
	<col />
	<col />
	<col />
	<col />
	<col width="300"/>
<thead>
<tr>
	<th>일련번호</th>
	<th>서버 HOST</th>
	<th>서버 PORT</th>
	<th>상태</th>
	<th>등록일</th>
	<th>기능</th>
</tr>
</thead>
<tbody>
<tfoot>
<tr>
	<td>신규등록</td>
	<td><input type='text' name='newHost' /></td>
	<td><input type='text' name='newPort' /></td>
	<td>
		<select class='stateOk' name='newState'>
			<option value='1' class='stateOk'>정상</option>
			<option value='0' class='stateHalt'>중지</option>
		</select>
	</td>
	<td>&nbsp;</td>
	<td>
		<button id='register'>등록</button>
	</td>
</tr>
</tfoot>
</tbody>
</table>