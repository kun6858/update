<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ include file="/resources/import/taglibs.jsp"%>
<%@ page trimDirectiveWhitespaces="true"%>
<script type="text/javascript">
	var packageInfo = eval(${resultMap.packageInfo});
	var fileSeq, appCmd;
	
	$(document).ready(init);

	function init() {
		//��Ű������ ���ϰ����� �̵��� ���
		var paramAppName = "${param.appname}";
		var paramAppCmd = "${param.appcmd}";
		var currentAppName = $("select[name='appNames']").val();
		var cuurentAppCmd = $("select[name='appCmds']").val();
		
		var appName = (paramAppName != "") ? paramAppName : currentAppName;
		var appCmd = (paramAppCmd != "") ? paramAppCmd : cuurentAppCmd;
		
		//��Ű���̸�, ��Ű�� Ŀ��� ����
		setPackage(appName, appCmd); 
		
		loadData();
		
		$("select[name='appNames'],select[name='appCmds']").change(function(){
			setPackage($("select[name='appNames']").val(), $("select[name='appCmds']").val());
			loadData();
		});
		
		//���Ͼ��ε�
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
			    if(uploadVal == "") { alert("������ �������ּ���"); return false; }
				options.url = options.url + "?app_cmd=" + $("select[name='appCmds']").val();
	        },
			success: function(responseText, statusText, xhr, $form) {
				if (statusText == 'success') {
					$("input[name='newfile']").val("");
					loadData();
				}
			}
		});
		
		//�������˾��ݱ�
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
		if(!confirm("WARNING!! WARNING!!\n������ ���������� �����Ͻðڽ��ϱ�?\n������ ������ �����ϽǼ� �����ϴ�."))
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
		//if(remoteFileName == "") { alert("remote�� �ʼ��׸��Դϴ�."); return false; }
		
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
				alert("����Ǿ����ϴ�.");
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
	
	/** ������� ��ϵ� ���� �Է� */
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
			if(data[key].state == 0) stateHTML = "<span class='stateHalt'>����</span>"; 
			else if(data[key].state == 1) stateHTML = "<span class='stateOk'>����</span>";
			else if(data[key].state == 2) { 
				stateHTML  = "<span class='stateNotReg'>�̵��</span><br/>";
			}
			else if(data[key].state == 3) stateHTML = "<span class='stateDeleted'>����</span>";
			
			//reg
			if(data[key].state == 0 || data[key].state == 1 || data[key].state == 2 || data[key].state == 3) { //xml�� ���� �����Ҷ��� ǥ��
				regHTML = "<input type='text' name='reg' rownum='"+parseInt(key+1)+"' seq='"+ data[key].seq +"' value='"+ data[key].reg +"'/>";
			}
			
			//etc
			etcHTML = "";
			switch(data[key].state) {
			case "0" :
				etcHTML += "<button name='modify' seq='"+ data[key].seq +"'>����</button>";
				etcHTML += "<button name='state' seq='"+ data[key].seq +"'><span class='stateHalt'>����</span></button>";
				etcHTML += "<button name='delete' seq='"+ data[key].seq +"'>����</button>";
				etcHTML += "<button name='delPhysicFile' rownum='"+parseInt(key+1)+"'>���ϻ���</button>";
				etcHTML += "<button name='reserveFile' seq='"+ data[key].seq +"'>�������</button>";
				break;
			case "1" :
				etcHTML += "<button name='modify' seq='"+ data[key].seq +"'>����</button>";
				etcHTML += "<button name='state' seq='"+ data[key].seq +"'><span class='stateOk'>����</span></button>";
				etcHTML += "<button name='delete' seq='"+ data[key].seq +"'>����</button>";
				etcHTML += "<button name='delPhysicFile' rownum='"+parseInt(key+1)+"'>���ϻ���</button>";
				etcHTML += "<button name='reserveFile' seq='"+ data[key].seq +"'>�������</button>";
				break;
			case "2" :
				if(isStartWithNumber(data[key].localFileName))
					etcHTML += "<strong>����</strong>�� �����ϴ� ������ ����� �� �����ϴ�.";
				else if(hasSpecialCharacter(data[key].localFileName))
					etcHTML += "<strong>Ư������</strong>�Ǵ� <strong>����</strong>�� �����ϴ� ���ϸ��� ����� �� �����ϴ�.";
				else
					etcHTML += "<button name='register' rownum='"+parseInt(key+1)+"'>���</button>";
					etcHTML += "<button name='delPhysicFile' rownum='"+parseInt(key+1)+"'>���ϻ���</button>";
				
				break;
			case "3" :
				etcHTML += "<button name='modify' seq='"+ data[key].seq +"'>����</button>";
				etcHTML += "<button name='delete' seq='"+ data[key].seq +"'>����</button>";
			}
			
			//version
			if(data[key].state == 0 || data[key].state == 1) { //xml�� ���� �����Ҷ��� ǥ��
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
		
		$(':button[name="delete"]').click(delFile); //���ϻ���
		$(':button[name="register"]').click(addFile); //���ϵ��
		$(':button[name="state"]').click(modState); //���º���
		$(':button[name="modify"]').click(modFile); //������������
		$(':button[name="newVersion"]').click(renewVersion); //��������
		$(':button[name="delPhysicFile"]').click(delPhysicFile); //���ϻ���
		$(':button[name="reserveFile"]').click(openReservePopup); //�˾�����
		
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
		
		$("#localTitle").html("�θ����� : " + $("input[name='localFileName'][seq='"+fileSeq+"']").val());
		
		loadReserveData();
		var layerElement = $("#reserveFileLayer");
		layerElement.hide();
		
		var left = ( $(window).scrollLeft() + ($(window).width() - layerElement.width()) / 2 );
		var top = ( $(window).scrollTop() + ($(window).height() - layerElement.height()) / 2 );
		layerElement.css({'left':left,'top':'30%', 'position':'absolute'});
		$('body').css('position','relative').append(layerElement);
		layerElement.fadeIn(500);
		
		//�������Ͼ��ε�
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
			    if(uploadVal == "") { alert("������ �������ּ���"); return false; }
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
		//tbody �ϼ�
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
				case "0" : //����
					stateMsg = "����"; 
					stateClass = "stateHalt";
					
					etcHTML = 
						"<button seq='"+seq+"' name='modReserve'>����</button>" +
						"<button seq='"+seq+"'name='modState'><span class='stateHalt'>����</span></button>" +
						"<button seq='"+seq+"'name='delPhysicReserve'>���ϻ���</button>";
						//"<button seq='"+seq+"'name='replaceReserve'>����ġȯ</button>";
					break;
					
				case "1" : //����
					stateMsg = "����";
					stateClass = "stateOk";
					
					etcHTML = 
						"<button seq='"+seq+"'name='modReserve'>����</button>" +
						"<button seq='"+seq+"'name='modState'><span class='stateOk'>����</button>" +
						"<button seq='"+seq+"'name='delPhysicReserve'>���ϻ���</button>";
						//"<button seq='"+seq+"'name='replaceReserve'>����ġȯ</button>";
						
					break;
				case "3" : //���ϻ���
					stateMsg = "������";
					stateClass = "stateDeleted";
					
					etcHTML = 
						"<button seq='"+seq+"'name='delReserve'>����</button>";

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
				"<tr><td colspan='8'>�ڷᰡ �����ϴ�.</td></tr>";
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
		
		//���������̺�Ʈ
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
		
		//Reserve DB���� (delReserve)
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
		
		//Reserve �������� ����
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
		
		//Reserve ���º���
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
		
		//Reserve ����
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
		
		//Reserve ����ġȯ
		$(":button[name='replaceReserve']").click(function(){
			if(!confirm("����ġȯ�� ���������� �����˴ϴ�."));
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
		
		//�޸���
		var currentAppCmd = $("select[name='appCmds']").val();
		var memo = packageInfo[packageInfo.length-1]["memo"][currentAppCmd];
		$("#packageMemo").html(memo);
	}
	
</script>
 
<div style="border-bottom: 1px dashed #ed8601; padding: 8px; margin-bottom: 8px;">
	<span id="abcd">��Ű��</span>
	<select name="appNames">
		<option value="����">����</option>
	</select>
	<select name="appCmds">
		<option value="����">����</option>
	</select>
	<span id="packageMemo"></span>
	<div id="fileInputDiv">
		<form name='fileUpload' action='<c:url value='/ajax/file_upload.byto'/>' method='post' enctype='multipart/form-data'>
		<div id="progressBar">
			<span style="width: 80%"></span>
		</div>
		<input type='file' name='newfile' num="0" class='fileUpload'/>
		<input type='submit' id='fileUploadSubmit' value="���ε�">
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
		<th>�Ϸù�ȣ</th>
		<th>remote</th>
		<th>local</th>
		<th>tagName</th>
		<th>version</th>
		<th>type</th>
		<th>reg</th>
		<th>size</th>
		<th>����</th>
		<th>���</th>
	</tr>
	</thead>
	<tbody id="tBody">
	</tbody>
</table>
<div id="reserveFileLayer">
	<div style="padding: 8px; margin-bottom: 8px;">
		<h3 id='localTitle' style=""></h3>
		<button id="closeReserveFileLayer">��</button>
		<div id="reserveUploadLayer" style="float: right;">
			<form name='reserveFileUpload' action='<c:url value='/ajax/upload_reserve_file.byto'/>' method='post' enctype='multipart/form-data'>
			<input type='file' name='newReservefile' num="0" class='fileUpload'/>
			<input type='submit' id='fileUploadSubmit' value="���ε�">
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
			<th>���ϸ�</th>
			<th>Rev</th>
			<th>����</th>
			<th>����</th>
			<th>����<br/>�ð�</th>
			<th>�޸�</th>
			<th>���</th>
		</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>