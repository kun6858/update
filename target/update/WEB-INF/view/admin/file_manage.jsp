<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<script type="text/javascript">
	var packageInfo = eval(${resultMap.packageInfo});
	var fileSeq, appCmd;
	
	$(document).ready(init);

	function init() {
		//패키지에서 파일관리로 이동한 경우
		var paramAppName = "${param.appname}";
		var paramAppCmd = "${param.appcmd}";
		var currentAppName = $("select[name='appNames']").val();
		var cuurentAppCmd = $("select[name='appCmds']").val();
		
		var appName = (paramAppName != "") ? paramAppName : currentAppName;
		var appCmd = (paramAppCmd != "") ? paramAppCmd : cuurentAppCmd;
		
		//패키지이름, 패키지 커멘드 설정
		setPackage(appName, appCmd); 
		
		loadData();
		
		$("select[name='appNames'],select[name='appCmds']").change(function(){
			setPackage($("select[name='appNames']").val(), $("select[name='appCmds']").val());
			loadData();
		});
		
		//파일업로드
		$("form[name='fileUpload']").ajaxForm({
			complete: function(xhr) {
				$("#progressBar").css("display", "none");
			},
			beforeSend: function() {
				$("#progressBar > span").css("width", "0%");
				$("#progressBar").css("display", "block");
			},
			uploadProgress: function(event, position, total, percentComplete) {
		        var percentVal = percentComplete + '%';
				$("#progressBar > span").css("width", percentVal);
		    },
			beforeSubmit: function(formData, jqForm, options) {
				var uploadVal = $("input[name='newfile']").val();
			    if(uploadVal == "") { alert("파일을 선택해주세요"); return false; }
				options.url = options.url + "?app_cmd=" + $("select[name='appCmds']").val();
	        },
			success: function(responseText, statusText, xhr, $form) {
				if (statusText == 'success') {
					$("input[name='newfile']").val("");
					loadData();
				}
			}
		});
		
		//예약등록팝업닫기
		$("#closeReserveFileLayer").click(closeReservePopup);
	}
	
	function delFile() {
		var seq = $(this).attr("seq");

		$.ajax({
			type : "POST",
			url : "<c:url value='/ajax/del_file.byto'/>",
			data : {
				seq : seq
			},
			success : loadData,
			error: ajaxErrorHandler
		});
	}
	
	function delPhysicFile() {
		if(!confirm("WARNING!! WARNING!!\n정말로 물리파일을 삭제하시겠습니까?\n삭제된 파일은 복구하실수 없습니다."))
			return false;
		
		var rownum = $(this).attr("rownum");
		var localFileName = $("input[name='localFileName'][rownum='"+rownum+"']").val();
		var appCmd = $("select[name='appCmds']").val();
		
		$.ajax({
			type : "POST",
			url : "<c:url value='/ajax/del_physic_file.byto'/>",
			data : {
				app_cmd : appCmd,
				local_file_name : localFileName
			},
			success : loadData,
			error: ajaxErrorHandler
		});
	}
	
	function addFile() {
		var rownum = $(this).attr("rownum");
		var selectedAppCmd = $("select[name='appCmds']").val();
		var remoteFileName = $('input[name="remoteFileName"][rownum="'+rownum+'"]').val();
		var localFileName = $('input[name="localFileName"][rownum="'+rownum+'"]').val();
		var reg = $('input[name="reg"][rownum="'+rownum+'"]').val();
		var tagName = $('input[name="tagName"][rownum="'+rownum+'"]').val();
		var type = $('input[name="type"][rownum="'+rownum+'"]').val();
		//if(remoteFileName == "") { alert("remote는 필수항목입니다."); return false; }
		
		$.ajax({
			type : "POST",
			url : "<c:url value='/ajax/add_file.byto'/>",
			data : {
				selected_app_cmd : selectedAppCmd,
				remote_file_name : remoteFileName,
				local_file_name : localFileName,
				reg : reg,
				tag_name : tagName,
				type : type
			},
			success : loadData,
			error: ajaxErrorHandler
		});
	}
	
	function modState() {
		var seq = $(this).attr("seq");
		
		$.ajax({
			type : "POST",
			url : "<c:url value='/ajax/mod_file_state.byto'/>",
			data : {
				seq : seq
			},
			success : loadData,
			error: ajaxErrorHandler
		});
	}
	
	function modFile() {
		var seq = $(this).attr("seq");
		var remoteFileName = $('input[name="remoteFileName"][seq="'+ seq +'"]').val();
		var reg = $('input[name="reg"][seq="'+ seq +'"]').val();
		var version = $('input[name="version"][seq="'+ seq +'"]').val();
		var tagName = $('input[name="tagName"][seq="'+seq+'"]').val();
		var type = $('input[name="type"][seq="'+seq+'"]').val();
		
		$.ajax({
			type : "POST",
			url : "<c:url value='/ajax/mod_file.byto'/>",
			data : {
				seq : seq,
				remote_file_name : remoteFileName,
				reg : reg,
				tag_name : tagName,
				type : type,
				version : version
			},
			success : function(html) {
				alert("변경되었습니다.");
			},
			error: ajaxErrorHandler
		});
	}
	
	function renewVersion() {
		var seq = $(this).attr("seq");

		$.ajax({
			type : "POST",
			url : "<c:url value='/ajax/mod_file_version.byto'/>",
			data : {
				seq : seq
			},
			success : loadData,
			error: ajaxErrorHandler
		});
	}

	
	function loadData() {
		tdHTML = "";
		selectedAppName = $("select[name='appNames']").val();
		selectedAppCmd = $("select[name='appCmds']").val();
		
		$.ajax({
			url : "<c:url value='/ajax/list_file.byto'/>",
			dataType : 'json',
			data : {
				app_name : selectedAppName,
				app_cmd : selectedAppCmd
			},
			success : function(data) {
				makeHTML(data);
				$('select[name="state"]').change(syncColor);
				fillReserveCount(selectedAppCmd);
			},
			error : function(a, b, c){ $("#tBody").html(""); ajaxErrorHandler(a,b,c); }
		});
	}
	
	/** 예약관리 등록된 갯수 입력 */
	function fillReserveCount(selectedAppCmd){
		$.ajax({
			url : "<c:url value='/ajax/fill_reserve_count.byto'/>",
			dataType : 'json',
			data : {
				app_cmd : selectedAppCmd,
			},
			success : function(data) {
				var seqlist =  data[0]["seqlist"];
				
				$.each(seqlist, function(i, e) {
					var count = data[0][e];
					if(count != 0)
						$("button[name='reserveFile'][seq='"+e+"']").append("("+count+")");
				});
			},
			error : function(a, b, c){ $("#tBody").html(""); ajaxErrorHandler(a,b,c); }
		});
	}
	
	function isStartWithNumber(input) {
		var regex = /^[0-9]/g; 

		if(regex.test(input)) {
		  return true;
		} else {
		  return false;
		}
		
		
		var firstChar = txt.substr(0, 1);
		for(var i=0; i<10; i++) {
			if(i==firstChar) return true;
		} 
		return false;
	}
	
	function hasSpecialCharacter(input) {
		var regex = /[\!\@\#\$\%\^\&\*\(\)\/\*\+\/\\\= ]/g; 

		if(regex.test(input)) {
		  return true;
		} else {
		  return false;
		}
	}
	
	function makeHTML(data) {
		
		$.each(data, function(key, val){
			//state		
			if(data[key].state == 0) stateHTML = "<span class='stateHalt'>중지</span>"; 
			else if(data[key].state == 1) stateHTML = "<span class='stateOk'>정상</span>";
			else if(data[key].state == 2) { 
				stateHTML  = "<span class='stateNotReg'>미등록</span><br/>";
			}
			else if(data[key].state == 3) stateHTML = "<span class='stateDeleted'>삭제</span>";
			
			//reg
			if(data[key].state == 0 || data[key].state == 1 || data[key].state == 2 || data[key].state == 3) { //xml에 파일 존재할때만 표시
				regHTML = "<input type='text' name='reg' rownum='"+parseInt(key+1)+"' seq='"+ data[key].seq +"' value='"+ data[key].reg +"'/>";
			}
			
			//etc
			etcHTML = "";
			switch(data[key].state) {
			case "0" :
				etcHTML += "<button name='modify' seq='"+ data[key].seq +"'>적용</button>";
				etcHTML += "<button name='state' seq='"+ data[key].seq +"'><span class='stateHalt'>중지</span></button>";
				etcHTML += "<button name='delete' seq='"+ data[key].seq +"'>삭제</button>";
				etcHTML += "<button name='delPhysicFile' rownum='"+parseInt(key+1)+"'>파일삭제</button>";
				etcHTML += "<button name='reserveFile' seq='"+ data[key].seq +"'>예약관리</button>";
				break;
			case "1" :
				etcHTML += "<button name='modify' seq='"+ data[key].seq +"'>적용</button>";
				etcHTML += "<button name='state' seq='"+ data[key].seq +"'><span class='stateOk'>정상</span></button>";
				etcHTML += "<button name='delete' seq='"+ data[key].seq +"'>삭제</button>";
				etcHTML += "<button name='delPhysicFile' rownum='"+parseInt(key+1)+"'>파일삭제</button>";
				etcHTML += "<button name='reserveFile' seq='"+ data[key].seq +"'>예약관리</button>";
				break;
			case "2" :
				if(isStartWithNumber(data[key].localFileName))
					etcHTML += "<strong>숫자</strong>로 시작하는 파일은 등록할 수 없습니다.";
				else if(hasSpecialCharacter(data[key].localFileName))
					etcHTML += "<strong>특수문자</strong>또는 <strong>공백</strong>를 포함하는 파일명은 등록할 수 없습니다.";
				else
					etcHTML += "<button name='register' rownum='"+parseInt(key+1)+"'>등록</button>";
					etcHTML += "<button name='delPhysicFile' rownum='"+parseInt(key+1)+"'>파일삭제</button>";
				
				break;
			case "3" :
				etcHTML += "<button name='modify' seq='"+ data[key].seq +"'>적용</button>";
				etcHTML += "<button name='delete' seq='"+ data[key].seq +"'>삭제</button>";
			}
			
			//version
			if(data[key].state == 0 || data[key].state == 1) { //xml에 파일 존재할때만 표시
				versionHTML =
					"<input name='version' seq='"+ data[key].seq +"' style='width:60%' type='text' value='"+data[key].version+"' />" +
					"<button class='btnVersion' name='newVersion' seq='"+ data[key].seq +"'>+</button>";
			} else {
				versionHTML = data[key].version
			}
			
			tdHTML += "<tr>";
			tdHTML += "<td>" + parseInt(key+1) +"</td>";
			tdHTML += "<td><input type='text' seq='"+ data[key].seq +"' rownum='"+parseInt(key+1)+"' name='remoteFileName' value='" + data[key].remoteFileName +"'/></td>";
			tdHTML += "<td>" + data[key].localFileName + "<input type='text' seq='"+ data[key].seq +"' rownum='"+parseInt(key+1)+"' name='localFileName' value='" + data[key].localFileName +"' readonly style='display: none;' /></td>";
			tdHTML += "<td><input type='text' seq='"+ data[key].seq +"' rownum='"+parseInt(key+1)+"' name='tagName' value='"+data[key].tagName+"'/></td>";
			tdHTML += "<td>" + versionHTML + "</td>";
			tdHTML += "<td><input type='text' seq='"+ data[key].seq +"' rownum='"+parseInt(key+1)+"' name='type' value='"+data[key].type+"'/></td>";
			tdHTML += "<td>" + regHTML +"</td>";
			tdHTML += "<td>" + comma(data[key].size) +"</td>";
			tdHTML += "<td>" + stateHTML +"</td>";
			tdHTML += "<td>" + etcHTML +"</td>";
			tdHTML += "</tr>";
			
		});
		$("#tBody").html(tdHTML);
		
		$(':button[name="delete"]').click(delFile); //파일삭제
		$(':button[name="register"]').click(addFile); //파일등록
		$(':button[name="state"]').click(modState); //상태변경
		$(':button[name="modify"]').click(modFile); //파일정보변경
		$(':button[name="newVersion"]').click(renewVersion); //버젼갱신
		$(':button[name="delPhysicFile"]').click(delPhysicFile); //파일삭제
		$(':button[name="reserveFile"]').click(openReservePopup); //팝업오픈
		
		$('input[name="version"]').datetimepicker({
			showOn: "button",
			dateFormat : 'yymmdd',
			timeFormat : 'hhmmss',
			showSecond: true,
			separator : ''
		}); //DateTimePicker
	}
	
	function openReservePopup(event) {
		fileSeq = $(this).attr("seq");
		appCmd = $("select[name='appCmds']").val();
		
		$("#localTitle").html("부모파일 : " + $("input[name='localFileName'][seq='"+fileSeq+"']").val());
		
		loadReserveData();
		var layerElement = $("#reserveFileLayer");
		layerElement.hide();
		
		var left = ( $(window).scrollLeft() + ($(window).width() - layerElement.width()) / 2 );
		var top = ( $(window).scrollTop() + ($(window).height() - layerElement.height()) / 2 );
		layerElement.css({'left':left,'top':'30%', 'position':'absolute'});
		$('body').css('position','relative').append(layerElement);
		layerElement.fadeIn(500);
		
		//예약파일업로드
		$("form[name='reserveFileUpload']").ajaxForm({
			complete: function(xhr) {
				$("#progressBar").css("display", "none");
			},
			beforeSend : function() {
				$("#progressBar > span").css("width", "0%");
				$("#progressBar").css("display", "block");
			},
			uploadProgress: function(event, position, total, percentComplete) {
		        var percentVal = percentComplete + '%';
				$("#progressBar > span").css("width", percentVal);
		    },
			beforeSubmit: function(formData, jqForm, options) {
				var uploadVal = $("input[name='newReservefile']").val();
			    if(uploadVal == "") { alert("파일을 선택해주세요"); return false; }
				options.url = options.url + "?app_cmd=" + appCmd + "&file_seq=" + fileSeq;
	        },
			success: function(responseText, statusText, xhr, $form) {
				if(statusText == "success") {
					$("input[name='newReservefile']").val("");
					loadData();
					loadReserveData();
				}
			}
		});
	}
	
	function loadReserveData() {
		//tbody 완성
		$.ajax({
			url : "<c:url value='/ajax/get_reserve_info.byto'/>",
			dataType : 'json',
			data : {
				file_seq : fileSeq,
				app_cmd : appCmd
			},
			success : makeReserveHTML,
			error : ajaxErrorHandler
		});
	}
	
	function makeReserveHTML(data) {
		var HTML = "";
		
		$.each(data, function(i, e) {
			var state = e["state"];
			var stateMsg, stateClass;
			var seq = e["seq"];
			var etcHTML = "";
			var sel1, sel2;
			
			switch(state) {
				case "0" : //중지
					stateMsg = "중지"; 
					stateClass = "stateHalt";
					
					etcHTML = 
						"<button seq='"+seq+"' name='modReserve'>적용</button>" +
						"<button seq='"+seq+"'name='modState'><span class='stateHalt'>중지</span></button>" +
						"<button seq='"+seq+"'name='delPhysicReserve'>파일삭제</button>";
						//"<button seq='"+seq+"'name='replaceReserve'>강제치환</button>";
					break;
					
				case "1" : //정상
					stateMsg = "정상";
					stateClass = "stateOk";
					
					etcHTML = 
						"<button seq='"+seq+"'name='modReserve'>적용</button>" +
						"<button seq='"+seq+"'name='modState'><span class='stateOk'>정상</button>" +
						"<button seq='"+seq+"'name='delPhysicReserve'>파일삭제</button>";
						//"<button seq='"+seq+"'name='replaceReserve'>강제치환</button>";
						
					break;
				case "3" : //파일삭제
					stateMsg = "삭제됨";
					stateClass = "stateDeleted";
					
					etcHTML = 
						"<button seq='"+seq+"'name='delReserve'>삭제</button>";

						break;
			}
			
			HTML +=
				"<tr>" +
					"<td>" + parseInt(i+1) + "</td>" +
					"<td>" + e["orgFilename"] + "</td>" +
					//"<td>" + e["newFilename"] + "</td>" +
					"<td>" + e["revision"] + "</td>" +
					"<td>" +
						"<input type='text' name='reserveVersion' style='width:60%' seq='"+seq+"' value='" + e["version"] + "'/>" +
						"<button class='btnVersion' name='newVersion' seq='"+ seq +"'>+</button>" +
					"</td>" +
					"<td><span class='" + stateClass + "'>" + stateMsg + "</span></td>" +
					"<td><input type='' name='applyTime' seq='"+seq+"' value='" + e["applyTime"] + "' style='width: 70%;' /></td>" +
					"<td><textarea class='reserveMemo' name='reserveMemo' seq='"+seq+"'>" + e["memo"] + "</textarea></td>" +
					"<td style='text-align:left;'>" + etcHTML + "</td>" +
				"</tr>";
		});
		
		if(data == null || data == "") {
			HTML +=
				"<tr><td colspan='8'>자료가 없습니다.</td></tr>";
		}		
		
		$("#reservedFileInfo > tbody").html(HTML);
		
		$('input[name="reserveVersion"]').datetimepicker({
			showOn: "button",
			dateFormat : 'yymmdd',
			timeFormat : 'hhmmss',
			separator : '',
			showSecond: true
		}); //DateTimePicker

		$('input[name="applyTime"]').datetimepicker({
			showOn: "button",
			dateFormat : 'yymmdd',
			timeFormat : 'hhmm',
			separator : ''
		}); //DateTimePicker
		
		//버젼증가이벤트
		$(":button[name='newVersion']").click(function(){
			var seq = $(this).attr("seq");

			$.ajax({
				type : "POST",
				url : "<c:url value='/ajax/mod_reserve_version.byto'/>",
				dataType : 'json',
				data : {
					seq : seq
				},
				success : loadReserveData,
				error : ajaxErrorHandler
			});
		});
		
		//Reserve DB삭제 (delReserve)
		$(":button[name='delReserve']").click(function(){
			var seq = $(this).attr("seq");

			$.ajax({
				type : "POST",
				url : "<c:url value='/ajax/del_reserve.byto'/>",
				dataType : 'json',
				data : {
					seq : seq
				},
				success : function() {
					loadData();
					loadReserveData();
				},
				error : ajaxErrorHandler
			});
		});
		
		//Reserve 물리파일 삭제
		$(":button[name='modState']").click(function(){
			var seq = $(this).attr("seq");

			$.ajax({
				type : "POST",
				url : "<c:url value='/ajax/mod_state.byto'/>",
				dataType : 'json',
				data : {
					seq : seq
				},
				success : loadReserveData,
				error : ajaxErrorHandler
			});
		});
		
		//Reserve 상태변경
		$(":button[name='delPhysicReserve']").click(function(){
			var seq = $(this).attr("seq");

			$.ajax({
				type : "POST",
				url : "<c:url value='/ajax/del_physic_reserve.byto'/>",
				dataType : 'json',
				data : {
					seq : seq
				},
				success : loadReserveData,
				error : ajaxErrorHandler
			});
		});
		
		//Reserve 변경
		$(":button[name='modReserve']").click(function(){
			var seq = $(this).attr("seq");
			var version = $("input[name='reserveVersion'][seq="+seq+"]").val();
			var applyTime = $("input[name='applyTime'][seq="+seq+"]").val();
			var memo = $("textarea[name='reserveMemo'][seq='"+seq+"']").val();
			
			$.ajax({
				type : "POST",
				url : "<c:url value='/ajax/mod_reserve.byto'/>",
				dataType : 'json',
				data : {
					seq : seq,
					version : version,
					apply_time : applyTime,
					memo : memo
				},
				success : loadReserveData,
				error : ajaxErrorHandler
			});
		});
		
		//Reserve 강제치환
		$(":button[name='replaceReserve']").click(function(){
			if(!confirm("강제치환시 현재파일은 삭제됩니다."));
			var seq = $(this).attr("seq");
			
			$.ajax({
				type : "POST",
				url : "<c:url value='/ajax/replace_reserve.byto'/>",
				dataType : 'json',
				data : {
					seq : seq,
				},
				success : loadReserveData,
				error : ajaxErrorHandler
			});
		});
	}
	
	function closeReservePopup() {
		$("#reserveFileLayer").fadeOut(500);
	}
	
	function setPackage(appName, appCmd) {
		var selectedAppName = appName;
		var selectedAppCmd = appCmd;
		
		var appNameHTML = "";
		var appCmdHTML = "";
		var loopNo = 0;
		
		for(var i=0; i<packageInfo.length-1; i++) {
			var tmpAppName = packageInfo[i]["appName"];
			appNameHTML += "<option value='" + tmpAppName + "'>" + tmpAppName + "</option>";
			if(selectedAppName == tmpAppName) loopNo = i;
		}

		for(var i=0; i<packageInfo[loopNo]["appCmd"].length; i++) {
			var tmpAppCmd = packageInfo[loopNo]["appCmd"][i];
			appCmdHTML += "<option value='" + tmpAppCmd + "'>" + tmpAppCmd + "</option>";
		}
		
		$("select[name='appNames']").html(appNameHTML);
		$("select[name='appCmds']").html(appCmdHTML);
		
		$("select[name='appNames']").find("option[value='"+selectedAppName+"']").attr("selected", "true");
		$("select[name='appCmds']").find("option[value='"+selectedAppCmd+"']").attr("selected", "true");
		
		//메모설정
		var currentAppCmd = $("select[name='appCmds']").val();
		var memo = packageInfo[packageInfo.length-1]["memo"][currentAppCmd];
		$("#packageMemo").html(memo);
	}
	
</script>
 
<div style="border-bottom: 1px dashed #ed8601; padding: 8px; margin-bottom: 8px;">
	<span id="abcd">패키지</span>
	<select name="appNames">
		<option value="없음">없음</option>
	</select>
	<select name="appCmds">
		<option value="없음">없음</option>
	</select>
	<span id="packageMemo"></span>
	<div id="fileInputDiv">
		<form name='fileUpload' action='<c:url value='/ajax/file_upload.byto'/>' method='post' enctype='multipart/form-data'>
		<div id="progressBar">
			<span style="width: 80%"></span>
		</div>
		<input type='file' name='newfile' num="0" class='fileUpload'/>
		<input type='submit' id='fileUploadSubmit' value="업로드">
		</form>
	</div>
	<p style="clear:both"/>
</div>
<table id="information">
	<col width="40" />
	<col />
	<col />
	<col />
	<col width="185"/>
	<col width="90"/>
	<col width="90"/>
	<col/>
	<col width="80"/>
	<col width="280"/>
	<thead>
	<tr>
		<th>일련번호</th>
		<th>remote</th>
		<th>local</th>
		<th>tagName</th>
		<th>version</th>
		<th>type</th>
		<th>reg</th>
		<th>size</th>
		<th>상태</th>
		<th>기능</th>
	</tr>
	</thead>
	<tbody id="tBody">
	</tbody>
</table>
<div id="reserveFileLayer">
	<div style="padding: 8px; margin-bottom: 8px;">
		<h3 id='localTitle' style=""></h3>
		<button id="closeReserveFileLayer">×</button>
		<div id="reserveUploadLayer" style="float: right;">
			<form name='reserveFileUpload' action='<c:url value='/ajax/upload_reserve_file.byto'/>' method='post' enctype='multipart/form-data'>
			<input type='file' name='newReservefile' num="0" class='fileUpload'/>
			<input type='submit' id='fileUploadSubmit' value="업로드">
			</form>
		</div>
		<p style="clear:both"/>
	</div>
	<table id="reservedFileInfo">
		<col width="30"/>
		<col/>
		<col width="50"/>
		<col width="185"/>
		<col width="80"/>
		<col width="140"/>
		<col width="150"/>
		<col width="250"/>
		<thead>
		<tr>
			<th>No</th>
			<th>파일명</th>
			<th>Rev</th>
			<th>버젼</th>
			<th>상태</th>
			<th>예약<br/>시간</th>
			<th>메모</th>
			<th>기능</th>
		</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>