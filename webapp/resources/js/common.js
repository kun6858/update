$(document).ready(function() {
	$('#loading')
	.ajaxStart(function()
	{
		var layerElement = $(this);
		var left = ( $(window).scrollLeft() + ($(window).width() - layerElement.width()) / 2 );
		var top = ( $(window).scrollTop() + ($(window).height() - layerElement.height()) / 2 );
		layerElement.css({'left':left,'top':top, 'position':'absolute'});
		$('body').css('position','relative').append(layerElement);
		layerElement.show();
	})
	.ajaxStop(function()
	{
		//$(this).fadeOut(200);
		$(this).hide();
	});	
});


function ajaxErrorHandler(xhr, ajaxOptions, thrownError) {
	if(xhr.status && xhr.status != 200) {
		var error = eval("("+xhr.responseText+")");
		alert(error.errorMsg);
	}
}

function isValidRegEx(input, desc, regex) {
	if(!input) { alert(desc + "는 필수 항목입니다."); }
	
	if(regex.test(input)) return true; 
	else {	
		alert(desc + "의 형식이 잘못되었습니다."); 
		return false;
	}
}

function comma(n) {
	var reg = /(^[+-]?\d+)(\d{3})/; // 정규식
	n += ''; // 숫자를 문자열로 변환

	while (reg.test(n))
		n = n.replace(reg, '$1' + ',' + '$2');

	return n;
}

function syncColor() {
	var state = $(this).val();
	if(state == 0) { $(this).removeClass("stateOk"); $(this).addClass("stateHalt"); }
	else { $(this).removeClass("stateHalt"); $(this).addClass("stateOk"); }
}
