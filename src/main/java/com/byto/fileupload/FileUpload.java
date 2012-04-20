package com.byto.fileupload;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;

/**
 * �� ���������� ���ε� �� ������ �ѱ����ڵ��� �޸��Կ� ����
 * ������ �������� ���ڵ� Ÿ���� ���������ֱ� ���� Ŭ����
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
