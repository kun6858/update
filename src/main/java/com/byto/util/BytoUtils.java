package com.byto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;

public class BytoUtils {
	
	public static String lineToBr(String oldLine) {
		return oldLine.replaceAll("\n", "<br/>"); 
	}
	
	public static String bRtoLine(String oldLine) {
		return oldLine.replaceAll("<br/>", "\n");
	}
	
	public static String getSysdate() {
		return new Long(System.currentTimeMillis()).toString();
	}
	
	public static String deleteLastChar(String inputString) {
		return inputString.substring(0, inputString.length()-1);
	}
	
	public static String paramToString(HttpServletRequest request) {
		Enumeration enumeration = request.getParameterNames();
		StringBuilder sb = new StringBuilder();
		sb.append("===Parameters===\n");
		while(enumeration.hasMoreElements()) {
			String paramName = (String) enumeration.nextElement();
			sb.append(paramName);
			sb.append(" : ");
			sb.append(request.getParameter(paramName));
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public static String putZero(String number) {
		int convertNumber = Integer.valueOf(number); 
		if(convertNumber < 10) {
			return "0" + String.valueOf(convertNumber); 
		} else {
			return number;
		}
	}
	
	public static File getFileFromProps(String key) {
		ClassLoader cl = null;
		Properties props = null;
		URL url = null;
		FileInputStream fi = null;
		
		try {
			cl = Thread.currentThread().getContextClassLoader();
			if(cl == null)	cl = ClassLoader.getSystemClassLoader();
			url = cl.getResource("files.properties");
			fi = new FileInputStream(new File(url.toURI()));
			props = new Properties();
			props.load(fi);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		} finally {
			try { 
				if(fi != null) fi.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new File(props.getProperty(key));
	}
	
	public static String DateFormat(String unixtime, String pattern) {
		return DateFormatUtils.format(Long.parseLong(unixtime.trim()), pattern);
	}
	
	public static int getRandomInt(int min, int max) {
		Random oRandom = new Random();

		int i = min-1;
		
		while(!(min <= i && i <= max)) {
			i = oRandom.nextInt(max+1);
		}

	    return i;
	}
	
	public static String lpad(char charater, String sourceString, int digit) {
		int charLength = sourceString.length();
		StringBuilder sb = new StringBuilder();
		
		if(charLength < digit) {
			for(int i=1; i<digit; i++) {
				sb.append(charater);
			}
		}
		sb.append(sourceString);
		return sb.toString();
	}
		
	/** str이 두번 연속 나오면 하나만 남기고 삭제 */
	public static String getRidOfDoubleChar(String str, String txt) {
		int index;
		while((index = txt.indexOf(str)) != -1) {
			txt = txt.replaceAll(str, "/");
		}
		return txt;
	}

	public static String getToday() {
		return DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
	}
	
	public static String getFileExtension(File file) {
		java.io.File targetFile = file;
		
		int index = targetFile.getName().lastIndexOf('.');
		if (index > 0 && index <= targetFile.getName().length() - 2 ) {
			return targetFile.getName().substring(index+1);
		}
		
		return "";
	}
	
	/** 3자리마다 점찍기 */
	public static String toCommifyString(String sourceNum) {
		if (sourceNum.isEmpty())
			return "0";
		if (sourceNum.length() <= 3)
			return sourceNum;

		double num = Double.parseDouble(sourceNum);
		DecimalFormat df = new DecimalFormat("#,###");

		return df.format(num).toString();
	}
	
	/** json 보기 좋게 만들어야지 */
	public static String JSONFormatter(String JSON) {
		StringBuilder newJSON = new StringBuilder();

		boolean isDone = false;
		int tabDept = 0;
		
		for(int i=0; i<JSON.length(); i++) {
			char charI = JSON.charAt(i);
			
			if(charI == '[' || charI == '{') {
				
				++tabDept;
				
				newJSON.append(charI);
				
				newJSON.append("\n");
				for(int x=0; x < tabDept; x++) {
					newJSON.append("\t");	
				}
				
			} else if(charI == ']' || charI == '}') {
				
				--tabDept;
				
				for(int x=0; x < tabDept; x++) {
					newJSON.append("\t");	
				}
				
				newJSON.append("\n");
				
				for(int x=0; x < tabDept; x++) {
					newJSON.append("\t");	
				}
				
				newJSON.append(charI);
				
			} else if(charI == ',') {
				
				if(JSON.charAt(i-1) == '}' || JSON.charAt(i-1) == '"') {
					
					newJSON.append(charI);
					
					newJSON.append("\n");
					
					for(int x=0; x < tabDept; x++) {
						newJSON.append("\t");	
					}
				} else {
					newJSON.append(charI);
				}
				
			} else {
				
				newJSON.append(charI);
				
			}
		}
		
		System.out.println(newJSON.toString());
		
		return newJSON.toString();
	}
	
}
