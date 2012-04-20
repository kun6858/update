package com.byto.fileupload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;

/**
 * 각 브라우저마다 업로드 된 파일의 한글인코딩을 달리함에 따라
 * 각각의 브라우저별 인코딩 타입을 재지정해주기 위한 클래스
 * @author JHK
 *
 */
public class FileUpload {
	
	public static final String SEP = System.getProperties().getProperty("file.separator");
	
	public static MultipartRequest upload(HttpServletRequest request, String PATH) 
			throws IOException {
		
		String encoding = "";
		
		String userAgent = request.getHeader("User-Agent");
		if (userAgent != null && userAgent.indexOf("MSIE 5.5") > -1) { 
			encoding = "KSC5601";
		} else if (userAgent != null && userAgent.indexOf("MSIE") > -1) { 
			encoding = "KSC5601";
		} else { 
			encoding = "UTF-8";
		}
		
		return 	new MultipartRequest(request, PATH, 999999999, encoding);
	}
}
