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
	if(!input) { alert(desc + "�� �ʼ� �׸��Դϴ�."); }
	
	if(regex.test(input)) return true; 
	else {	
		alert(desc + "�� ������ �߸��Ǿ����ϴ�."); 
		return false;
	}
}

function comma(n) {
	var reg = /(^[+-]?\d+)(\d{3})/; // ���Խ�
	n += ''; // ���ڸ� ���ڿ��� ��ȯ

	while (reg.test(n))
		n = n.replace(reg, '$1' + ',' + '$2');

	return n;
}

function syncColor() {
	var state = $(this).val();
	if(state == 0) { $(this).removeClass("stateOk"); $(this).addClass("stateHalt"); }
	else { $(this).removeClass("stateHalt"); $(this).addClass("stateOk"); }
}
