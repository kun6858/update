<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<script type="text/javascript">
$(document).ready(function() {
	init(); //�ʱ�ȭ
});

function init() {
	loading();
	$(':button[id="register"]').click(addServer); //���� ���
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
		var stateMsg = (state == 1) ? "<span class='stateOk'>����</span>" : "<span class='stateHalt'>����</span>";
		var stateClass = (state == 1) ? "stateOk" : "stateHalt";
		
		HTML += "<tr>";
		HTML += "<td>" + parseInt(key+1) + "</td>";
		HTML += "<td><input type='text' id='host_" + seq + "' value='" + host + "'/></td>";
		HTML += "<td><input type='text' id='port_" + seq + "' value='" + port + "'/></td>";
		HTML += "<td class='" + stateClass + "'>" + stateMsg + "</td>";
		HTML +=	"<td>" + regDate + "</td>";
		HTML +=	"<td>";
		HTML +=	"<button name='modify' value='" + seq + "'>����</button>";
		HTML += "<button name='state' state='"+state+"' value='" + seq + "'>" + stateMsg + "</button>";
		HTML += "<button name='delete' value='" + seq + "'>����</button>";
		HTML += "</td>";
		HTML += "</tr>";
	});
	
	$("tbody").html(HTML);
	
	$(':button[name="state"]').click(modServerState); //���� ���� ����
	$(':button[name="modify"]').click(modServer); //���� ������ or ȣ��Ʈ or ��Ʈ ����
	$(':button[name="delete"]').click(delServer); //���� ����
}

function addServer(event) {
	var host =  $('input[name="newHost"]').val();
	var port =  $('input[name="newPort"]').val();
	var state = $('select[name="newState"]').val();
	
	if(!host) { alert("���� HOST�� �ʼ��׸��Դϴ�."); return false; }
	if(!port) { alert("���� PORT�� �ʼ��׸��Դϴ�."); return false; }
	if(!state) { alert("���� STATE�� �ʼ��׸��Դϴ�."); return false; }
	
	if(!confirm("����Ͻðڽ��ϱ�?")) return false;
	
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
	if(!confirm("������ �����Ͻðڽ��ϱ�?")) return false;
	
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
	
	if(!host) { alert("���� HOST�� �ʼ��׸��Դϴ�."); return false; }
	if(!port) { alert("���� PORT�� �ʼ��׸��Դϴ�."); return false; }

	if(!confirm("���������� �����Ͻðڽ��ϱ�?")) return false;
	
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
			alert("���������� ���������� ����Ǿ����ϴ�.");
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
	<th>�Ϸù�ȣ</th>
	<th>���� HOST</th>
	<th>���� PORT</th>
	<th>����</th>
	<th>�����</th>
	<th>���</th>
</tr>
</thead>
<tbody>
<tfoot>
<tr>
	<td>�űԵ��</td>
	<td><input type='text' name='newHost' /></td>
	<td><input type='text' name='newPort' /></td>
	<td>
		<select class='stateOk' name='newState'>
			<option value='1' class='stateOk'>����</option>
			<option value='0' class='stateHalt'>����</option>
		</select>
	</td>
	<td>&nbsp;</td>
	<td>
		<button id='register'>���</button>
	</td>
</tr>
</tfoot>
</tbody>
</table>