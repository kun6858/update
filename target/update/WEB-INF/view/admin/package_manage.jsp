<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<script type="text/javascript">
$(document).ready(init);

function init() {
	loading();
	$("#regHour").click(regHour); //시간등록 
	$(':button[id="register"]').click(addPackage); //패키지 등록
	$('#H_start_hour').change(makeHourValid);
	$('#H_end_hour').change(makeHourValid);
	$("select[name='newState']").change(syncColor);
}

function loading(){
	tdHTML = "";
	$("#hourLayer").hide();
	$("#hourLayer").appendTo("#originalPoint");
	
	$.ajax({
		url : "<c:url value='/ajax/list_package.byto'/>",
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
		var appName = data[key].appName;
		var appCmd = data[key].appCmd;
		var version = data[key].version;
		var path = data[key].path;
		var remotePrefix = data[key].remotePrefix;
		var state = data[key].state;
		var memo = data[key].memo;
		var use = data[key].use;
		var useMsg = (use == 1) ? "시간사용" : "시간중지";
		var useClass = (use == 1) ? "hourUse" : "hourNotUse";
		var stateMsg = (state == 1) ? "<span class='stateOk'>정상</span>" : "<span class='stateHalt'>중지</span>";
		var stateClass = (state == 1) ? "stateOk" : "stateHalt";
		
		HTML += "<tr>";
		HTML += "<td class='appendPoint' seq='" + seq + "'><input type='text' name='appName' seq='" + seq + "' value='" + appName + "'/></td>";
		HTML += "<td><input type='text' name='appCmd' seq='" + seq + "' value='" + appCmd + "'/></td>";
		HTML += "<td><input style='width:60%' type='text' name='version' seq='" + seq + "' value='"+version+"'/><button name='newVersion' seq='" + seq + "'>+</button></td>";
		HTML += "<td name='serverPanel' id='serverPanel' appCmd='" + appCmd + "'></td>";
		HTML += "<td><input type='text' name='path' seq='" + seq + "' value='" + path + "'/></td>";
		HTML += "<td><input type='text' name='remotePrefix' seq='" + seq + "' value='" + remotePrefix + "'/></td>";
		HTML += "<td id='limit_" + appCmd + "' name='limitCountPanel' appCmd='" + appCmd + "'></td>";
		HTML += "<td class='"+stateClass+"'>"+stateMsg+"</td>";
		HTML += "<td><textarea id='memo' name='memo' seq='" + seq + "'>"+memo+"</textarea></td>";
		HTML += "<td>";
		HTML += "<button name='modify' seq='" + seq + "'>적용</button>";
		HTML += "<button name='delete' seq='" + seq + "'>삭제</button>";
		HTML += "<button name='state' seq='" + seq + "'>"+stateMsg+"</button>";
		HTML += "<a href='<c:url value='/update.jsp'/>?cmd=" + appCmd + "' target='_blank'><button name='get_xml' seq='" + seq + "'>XML</button></a>";
		HTML += "<button name='use' class='"+useClass+"' seq='" + seq + "'>"+useMsg+"</button>";
		HTML += "<button name='reg_hour' seq='" + seq + "'>시간등록</button>";
		HTML += "<a href='<c:url value='/admin/file_manage.byto'/>?appname="+appName+"&appcmd=" + appCmd + "'><button name='fileManage' seq='" + seq + "'>파일관리</button></a>";
		HTML += "</td>";

	});
	
	$("tbody#appendPoint").html(HTML);
	
	$('td[name="limitCountPanel"]').each(showEachDownloadHour); //다운로드 시간표시
	$('td[name="serverPanel"]').each(showEachPackageServer); //등록된 서버표시
	$(':button[name="modify"]').click(modPackage); //패키지 정보변경
	$(':button[name="delete"]').click(delPackage); //패키지 삭제
	$(':button[name="state"]').click(modPackageState); //패키지 상태변경
	$(':button[name="use"]').click(setHourUse); //시간 사용여부변경
	$(':button[name="newVersion"]').click(versionRenewal); //버전 갱신
	$(':button[name="reg_hour"]').click(openHourLayer); //시간등록 레이어오픈
	$('#closeHourLayer').click(closeHourLayer); //시간등록 레이어닫음
}

function makeHourValid() {
	var startHour = $('#H_start_hour').val();
	var endHour = $('#H_end_hour').val();
	
	if(parseInt(startHour) > parseInt(endHour)) { //from > to
		setHour(parseInt(startHour)+1, "end");
	} else if (parseInt(startHour) == parseInt(endHour)) { //from = to
		setHour(parseInt(startHour)+1, "end");
	}
} 

function setHour(hour, hourType) {
	$("#H_" + hourType + "_hour > option[value=" + hour + "]").attr("selected", "selected");
}

function regHour() {	
	var appCmd = $("#H_appCmd").val();
	var startHour = $("#H_start_hour").val();
	var endHour = $("#H_end_hour").val();
	var limitCount = $("#H_limitCount").val();
	
	$.ajax({
		type : "POST",
		url : "<c:url value='/ajax/add_download_hour.byto'/>",
		dataType : "json",
		data : {
			app_cmd : appCmd,
			start_hour : startHour,
			end_hour : endHour,
			limit_count : limitCount
		},
		success : function(){ closeHourLayer(); loading(); },
		error: ajaxErrorHandler
	});
}

function openHourLayer() {
	var seq = $(this).attr("seq");
	var appName = $('input[name="appName"][seq="'+ seq +'"]').val();
	var currentAppCmd = $('input[name="appCmd"][seq="'+ seq +'"]').val();
	var defaultAppCmd = $('input[name="appCmd"][seq="'+ seq +'"]')[0].defaultValue;
	
	if(defaultAppCmd != currentAppCmd) {
		alert("App cmd값이 변경되었습니다. \n새로고침이나 적용 후 사용해주세요");
		return false;
	}

	$("#H_appCmd").val(currentAppCmd);
	$("#H_appName").html(appName);
	
	if($("#hourLayer").css("display")=="block") {
		$("#hourLayer").fadeOut(500, function() {
			$("#hourLayer").appendTo("td.appendPoint[seq="+seq+"]");
		});		
	} else {
		$("#hourLayer").appendTo("td.appendPoint[seq="+seq+"]");
	}
	
	$("#hourLayer").fadeIn(500);
}

function closeHourLayer(){
	$("#H_start_hour > option[value='0']").attr("selected", "true");
	$("#H_end_hour > option[value='1']").attr("selected", "true");
	$("#H_limitCount > option[value='*']").attr("selected", "true");
	$("#hourLayer").fadeOut(500);
}

function showEachDownloadHour(i) {
	var appCmd = $(this).attr("appCmd");
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/get_each_download_info.byto'/>",
		data : { 
			app_cmd : appCmd
		},
		success : jsonToHtmlDownHour,
		error: ajaxErrorHandler
	});
}

function showEachPackageServer(i) {
	var appCmd = $(this).attr("appCmd");
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/get_each_package_server.byto'/>",
		data : { 
			app_cmd : appCmd
		},
		success : jsonToHtmlPackageServer,
		error: ajaxErrorHandler
	});
}

function jsonToHtmlPackageServer(data) {
	var HTML = "";
	var regServer = data[0]["regServer"];
	var notRegServer = data[0]["notRegServer"];
	var appCmd = data[0]["appCmd"];
	var host;
	var seq;
	
	$.each(regServer, function(key, val){
		host = val["host"];
		seq = val["seq"];
		
		HTML += "<div class='leftText'>";
		HTML += "<span class='deleteHour' name='del_package_server' appCmd='" + appCmd + "' host='"+ host +"'>[X]</span> ";
		HTML += "<span>"+ host +"</span> ";
		HTML += "</div>";
	});
	
	if(notRegServer.length != 0) {
		HTML += "<select style='margin-top: 8px;' appCmd='"+appCmd+"' name='packageServer'>";
		$.each(notRegServer, function(key, val){
			host = val["host"];
			HTML += "<option value='"+host+"'>"+host+"</option>";
		});
		HTML += "</select>";
		HTML += "<button style='margin-left:2px;' appCmd='"+appCmd+"' name='addPackageServer'>+</button>";
	}
	
	$("td[name='serverPanel'][appCmd='"+appCmd+"']").html(HTML);
	$('span[name="del_package_server"][appCmd="'+appCmd+'"]').click(delPakcageServer); //등록서버 표시
	$('button[name="addPackageServer"][appCmd="'+appCmd+'"]').click(addPackageServer); //패키지 연동 서버등록
}

function delPakcageServer() {
	var appCmd = $(this).attr("appCmd");
	var host = $(this).attr("host");

	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/del_package_server.byto'/>",
		data : {
			appCmd : appCmd,
			host : host
		},
		success : loading,
		error: ajaxErrorHandler
	});
}

function addPackageServer() {
	var appCmd = $(this).attr("appCmd");
	var host = $("select[name='packageServer'][appCmd='"+appCmd+"']").val();

	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/add_package_server.byto'/>",
		data : {
			appCmd : appCmd,
			host : host
		},
		success : loading,
		error: ajaxErrorHandler
	});
}

function jsonToHtmlDownHour(data) {
	var HTML = "";
	var appCmd; 
	
	$.each(data, function(key, val){
		appCmd = data[key]["appCmd"];
		var startHour = data[key]["startHour"];
		var endHour = data[key]["endHour"];
		var limitCount = data[key]["limitCount"];
		var seq = data[key]["seq"];
		
		HTML += "<div class='leftText' id='downInfo_"+ appCmd +"_"+ startHour +"'>";
		HTML += "<span class='deleteHour' name='del_limit_count' appCmd='" + appCmd + "' seq='" + seq + "'>[X]</span> ";
		HTML += "<span class='resetHour' name='reset_limit_count' appCmd='" + appCmd + "' startHour='"+startHour+"' endHour='"+endHour+"'>" + startHour + ":00 ~ " + endHour + ":00 : " + limitCount + "</span>";
		HTML += "</div>";
	});	
	
	$("#limit_" + appCmd).html(HTML);
	
	$('span[name="del_limit_count"]').click(delLimitHour); //다운로드 시간삭제
	
	//카운트 초기화
	$('span[name="reset_limit_count"]').click(function(event){
		event.stopImmediatePropagation();
		if(!confirm("해당시간의 다운로드횟수를 초기화합니다")) return false;
		var appCmd = $(this).attr("appCmd");
		var startHour = $(this).attr("startHour");
		var endHour = $(this).attr("endHour");

		$.ajax({
			type : "POST",
			dataType : "json",
			url : "<c:url value='/ajax/reset_count.byto'/>",
			data : {
				app_cmd : appCmd,
				start_hour : startHour,
				end_hour : endHour
			},
			success : loading,
			error: ajaxErrorHandler
		});
	});
}

function delLimitHour(event) {
	var seq = $(this).attr("seq");
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/del_download_hour.byto'/>",
		data : {
			seq : seq
		},
		success : loading,
		error: ajaxErrorHandler
	});
}

function modPackage() {
	var seq = $(this).attr("seq");
	var appName = $('input[name="appName"][seq="'+ seq +'"]').val();
	var appCmd = $('input[name="appCmd"][seq="'+ seq +'"]').val();
	var path = $('input[name="path"][seq="'+ seq +'"]').val();
	var version = $('input[name="version"][seq="'+ seq +'"]').val();
	var remotePrefix = $('input[name="remotePrefix"][seq="'+ seq +'"]').val();
	var memo = $('textarea[name="memo"][seq="'+ seq +'"]').val();
	
	if(!appName) { alert("App 이름은 필수항목입니다."); return false; }
	if(!appCmd) { alert("App cmd는 필수항목입니다."); return false; }
	if(!path) { alert("Path는 필수항목입니다."); return false; }

	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/mod_package.byto'/>",
		data : {
			seq : seq,
			app_name : appName,
			app_cmd : appCmd,
			path : path,
			remote_prefix : remotePrefix,
			memo : memo,
			version : version
		},
		success : function() { alert("변경되었습니다."); },
		error: ajaxErrorHandler
	});
}

function addPackage() {
	var appName = $('input[name="newAppName"]').val();
	var appCmd = $('input[name="newAppCmd"]').val();
	var path = $('input[name="newPath"]').val();
	var state = $('select[name="newState"]').val();
	var memo = $('textarea[name="newMemo"]').val();
	var remotePrefix = $('input[name="newRemotePrefix"]').val();
	
	if(!appName) { alert("App 이름은 필수항목입니다."); return false; }
	if(!appCmd) { alert("App cmd는 필수항목입니다."); return false; }
	if(!path) { alert("Path는 필수항목입니다."); return false; }
	
	if(!confirm("패키지를 등록하시겠습니까?")) return false;
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/add_package.byto'/>",
		data : {
			app_name : appName,
			app_cmd : appCmd,
			path : path,
			state : state,
			memo : memo,
			use : "0",
			remote_prefix : remotePrefix
		},
		success : function (){
			loading();
			$('input[name="newAppName"]').val("");
			$('input[name="newAppCmd"]').val("");
			$('input[name="newPath"]').val("");
			$('input[name="newRemotePrefix"]').val("");
			$('select[name="newState"][value="1"]').attr("selected", "true");
			$('textarea[name="newMemo"]').val("");
		},
		error: ajaxErrorHandler
	});
}

function delPackage() {
	if(!confirm("정말로 삭제하시겠습니까?")) return false;
	var seq = $(this).attr("seq");

	$.ajax({
		type : "POST",
		dataType: "json",
		url : "<c:url value='/ajax/del_package.byto'/>",
		data : {
			seq : seq
		},
		success : loading,
		error: ajaxErrorHandler
	});
}

function modPackageState() {
	var seq = $(this).attr("seq");
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/mod_package_state.byto'/>",
		data : {
			seq : seq
		},					
		success : loading,
		error: ajaxErrorHandler
	});
}

function setHourUse() {
	var seq = $(this).attr("seq");
	
	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/mod_package_use.byto'/>",
		data : {
			seq : seq
		},					
		success : loading,
		error: ajaxErrorHandler
	});
}

function versionRenewal() {
	var seq = $(this).attr("seq");

	$.ajax({
		type : "POST",
		dataType : "json",
		url : "<c:url value='/ajax/mod_package_version.byto'/>",
		data : {
			seq : seq
		},
		success : loading,
		error: ajaxErrorHandler
	});
}
</script>

<table id="information">
	<col/>
	<col/>
	<col width="130"/>
	<col width="165"/>
	<col/>
	<col/>
	<col width="165"/>
	<col/>
	<col width="200"/>
	<col width="210"/>
	<thead>
	<tr>
		<th>App 이름</th>
		<th>App cmd</th>
		<th>version</th>
		<th>등록서버</th>
		<th>경로</th>
		<th>prefix</th>
		<th>시간당 다운수</th>
		<th>상태</th>
		<th>메모</th>
		<th>기능</th>
	</tr>
	</thead>
	<tbody id="appendPoint">
	</tbody>
	<tfoot>
	<tr>
		<td><input type="text" name="newAppName" /></td>
		<td><input type="text" name="newAppCmd" /></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td><input type="text" name="newPath" /></td>
		<td><input type="text" name="newRemotePrefix" /></td>
		<td>&nbsp;</td>
		<td>
			<select class='stateOk' name="newState">
				<option value="1" class="stateOk">정상</option>
				<option value="0" class="stateHalt">중지</option>
			</select>
		</td>
		<td><textarea name="newMemo"></textarea></td>
		<td>
			<button id="register">등록</button>
		</td>
	</tr>
	</tfoot>
</table>
<div id="originalPoint">
<div id="hourLayer">
	<button id="closeHourLayer">×</button>
	<input type="text" id="H_appCmd" style="display: none;" readonly="readonly" />
	<h3>시간당 다운수 등록</h3>
	<table>
		<tr>
			<th>App이름</th>
			<td id="H_appName"></td>
		</tr>
		<tr>
			<th>시작/종료</th>
			<td>
				<select id="H_start_hour">
					<c:forEach begin="0" end="23" step="1" var="loop">
					<option value="${loop}">${loop}:00</option>
					</c:forEach>
				</select>
				~ 
				<select id="H_end_hour">
					<c:forEach begin="1" end="24" step="1" var="loop">
					<option value="${loop}">${loop}:00</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<th>제한 다운수</th>
			<td>
				<select id="H_limitCount">
					<option value="*">무제한</option>
					<option value="1">1</option>
					<c:forEach begin="5" end="500" step="5" var="loop">
					<option value="${loop}">${loop}</option>
					</c:forEach>
					<c:forEach begin="550" end="3980" step="50" var="loop">
					<option value="${loop}">${loop}</option>
					</c:forEach>
					<c:forEach begin="4000" end="9500" step="500" var="loop">
					<option value="${loop}">${loop}</option>
					</c:forEach>
					<c:forEach begin="10000" end="100000" step="5000" var="loop">
					<option value="${loop}">${loop}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td colspan="2" style="text-align:center;">
				<button id="regHour">등록</button>
			</td>
		</tr>
	</table>
</div>
</div>