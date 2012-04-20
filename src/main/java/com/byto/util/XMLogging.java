package com.byto.util;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XMLogging {
	
	private static int seq = 0;
	private static Log log = LogFactory.getLog(XMLogging.class);
	private static Map logMap;
	private static List logList = new ArrayList();
	
	public static void addLogList(String string) {
		if(logList == null) 
			logList = new ArrayList();
		
		logList.add(string);
	}
	
	public static void logListClear() {
		logList.clear();
	}
	
	/** 로그 등록 */
	public static void setLog(String key, String value) {
		if (logMap == null)
			logMap = new HashMap();
		logMap.put(key, value);
	}
	
	public static void clear() {
		logMap.clear();
	}

	/** 로그 리턴 */
	public static String getLog(String key) {
		return (String) logMap.get(key);
	}
	
	/** 로그 저장 
	 * @throws IOException */
	public static synchronized void flush() throws Exception {
		if (logMap != null && logMap.size() > 0) {
			Set keySet = logMap.keySet();
			Iterator i = keySet.iterator();
			StringBuilder txt = new StringBuilder();

			txt.append("<history>" + "\n");
			while (i.hasNext()) {
				String key = (String) i.next();
					txt.append("\t<" + key + ">");
					txt.append(logMap.get(key));
					txt.append("</" + key + ">" + "\n");
			}
			txt.append("</history>" + "\n");

			addLogList(txt.toString());
			
			log();
			
			clear();
		}
	}
	
	public static void log() throws IOException {
		if(++seq % 100 == 0) {
			Iterator i = logList.iterator();
			while(i.hasNext()) {
				writeToLogFile((String) i.next());
			}
			logListClear();
		}
	}
	
	public static void writeToLogFile(String string) throws IOException {
		File historyFile = BytoUtils.getFileFromProps("HISTORY");
		FileUtils.writeStringToFile(historyFile, string, true);
	}
}
