package com.byto.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.byto.vo.EntryContainer;
import com.byto.vo.Package;
import com.byto.vo.Server;

/**
 * XML파일에 ValueObject를 기록하기 위해서 필요한 CRUD 작업을 지원하는 클래스
 * @author JHK
 */
public class VOHelper {
	
	public static final File SERVER_FILE = BytoUtils.getFileFromProps("SERVER");
	public static final File DOWNLOAD_FILE = BytoUtils.getFileFromProps("DOWNLOAD");
	public static final File PACKAGE_FILE = BytoUtils.getFileFromProps("PACKAGE");
	public static final File FILE_FILE = BytoUtils.getFileFromProps("FILE");
	public static final File PACKAGESERVER_FILE = BytoUtils.getFileFromProps("PACKAGESERVER");
	public static final File RESERVE_FILE = BytoUtils.getFileFromProps("RESERVE");
	
	public static final XMLize SERVER_XMLIZE = new XMLize(com.byto.vo.Server.class);
	public static final XMLize DOWNLOAD_XMLIZE = new XMLize(com.byto.vo.Download.class);
	public static final XMLize PACKAGE_XMLIZE = new XMLize(com.byto.vo.Package.class);
	public static final XMLize FILE_XMLIZE = new XMLize(com.byto.vo.File.class);
	public static final XMLize PACKAGESERVER_XMLIZE = new XMLize(com.byto.vo.PackageServer.class);
	public static final XMLize RESERVE_XMLIZE = new XMLize(com.byto.vo.Reserve.class);
 
	/**
	 * 신규 ValueObject를 파일에 추가
	 * @param propName
	 * @param valueObject
	 * @throws Exception
	 */
	public static void addVO(String propName, Object valueObject) throws Exception {
		XMLize xmlize = VOHelper.getXMLize(propName);
		File file = VOHelper.getFile(propName);
		EntryContainer ec = getEntryContainer(xmlize, file);
		ec.addEntries(valueObject);
		
		if(ec.getEntries().get(0) instanceof Comparable)
			Collections.sort((List) ec.getEntries());
		
		VOHelper.writeStringToFile(file, VOHelper.toXML(xmlize, ec));
	}

	/** 단수 삭제 */
	public static void delVO(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		VOHelper.processVO(propName, searchMap, null, false, "delete"); //false means one!! 
	}
	
	/** 단수 삭제 */
	public static void delVO(String propName, Map searchMap) throws Exception {
		VOHelper.processVO(propName, searchMap, null, false, "delete"); //false means one!! 
	}
	
	/** 복수 삭제 */
	public static void delVOs(String propName, Map searchMap) throws Exception {
		VOHelper.processVO(propName, searchMap, null, true, "delete");
	}
	
	/** 복수 삭제 */
	public static void delVOs(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		VOHelper.processVO(propName, searchMap, null, true, "delete");
	}
	
	/** 단수치환 */
	public static void modVO(String propName, String searchKey, String searchVal, String replaceKey, String replaceVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		Map replaceMap = new HashMap();
		replaceMap.put(replaceKey, replaceVal);
		VOHelper.processVO(propName, searchMap, replaceMap, false, "modify");
	}
	
	/** 단수치환 */
	public static void modVO(String propName, Map searchMap, Map replaceMap) throws Exception {
		VOHelper.processVO(propName, searchMap, replaceMap, false, "modify");
	}
	
	/** 복수치환 */
	public static void modVOs(String propName, String searchKey, String searchVal, String replaceKey, String replaceVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		Map replaceMap = new HashMap();
		replaceMap.put(replaceKey, replaceVal);
		VOHelper.processVO(propName, searchMap, replaceMap, true, "modify");
	}
	
	/** 복수치환 */
	public static void modVOs(String propName, Map searchMap, Map replaceMap) throws Exception {
		VOHelper.processVO(propName, searchMap, replaceMap, true, "modify");
	}
	
	/** 단수 읽기 */
	public static Object getVO(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		
		return processVO(propName, searchMap, null, false, "read");
	}
	
	/** 단수 읽기 */
	public static Object getVO(String propName, Map<String, String> searchMap) throws Exception {
		return (Object) processVO(propName, searchMap, null, false, "read");
	}

	/** 복수 읽기 */
	public static List getVOList(String propName) throws Exception {
		XMLize xmlize = VOHelper.getXMLize(propName);
		File file = VOHelper.getFile(propName);
		return VOHelper.getEntryContainer(xmlize, file).getEntries();
	}
	
	/** 복수읽기 */
	public static List getVOList(String propName, Map searchMap) throws Exception {
		List list = (List) VOHelper.processVO(propName, searchMap, null, true, "read");
		if(list == null) return new ArrayList();
		return list;
	}
	
	/** 복수읽기 */
	public static List getVOList(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		
		return (List) VOHelper.getVOList(propName, searchMap);
	}
	
	/** VO 읽기, 삭제 또는 변경 처리 */ 
	private static Object processVO(String propName, Map<String, String> searchMap, Map<String, String> replaceMap, boolean isAll, String mode) throws Exception {
		XMLize xmlize = VOHelper.getXMLize(propName);
		File file = VOHelper.getFile(propName);
		EntryContainer ec = VOHelper.getEntryContainer(xmlize, file);
		List list = ec.getEntries();
		List resultList = new ArrayList();
		
		Iterator iterator = list.iterator();
		
		boolean isModified = false;
		boolean isAtLeastOneMatch = false;
		
		while(iterator.hasNext()) {
			Object valueObject = (Object) iterator.next();
			
			Set searchNameSet = searchMap.keySet();
			Iterator searchIterator = searchNameSet.iterator();

			int searchSize = searchNameSet.size();
			int matchedSize = 0;
			
			while(searchIterator.hasNext()) {
				String currentSearchName = (String) searchIterator.next();
				String getMethodName = "get" + currentSearchName.toUpperCase().substring(0, 1) + currentSearchName.substring(1);
				Method getMethod = valueObject.getClass().getMethod(getMethodName, null);
				
				String retString = (String) getMethod.invoke(valueObject, null);

				if(searchMap.get(currentSearchName).equals(retString)) {
					matchedSize++; 
				}
			}
			
			//매칭된 갯수가 틀리면 중단
			if(matchedSize != searchSize) continue; 
			else isAtLeastOneMatch = true; 
			
			//읽기모드일경우 VO리턴
			if("read".equals(mode)) {
				if(isAll == true)
					resultList.add(valueObject);
				else
					return (Object) valueObject;
			}
			
			//삭제모드일경우 VO삭제
			if("delete".equals(mode)) {
				//실제삭제
				iterator.remove();
				isModified = true;
			}

			//수정모드일경우 VO수정
			if("modify".equals(mode)) {
				Set replaceNameSet = replaceMap.keySet();
				Iterator replaceIterator = replaceNameSet.iterator();
				
				while(replaceIterator.hasNext()) {
					String currentReplaceName = (String) replaceIterator.next();
					String setMethodName = "set" + currentReplaceName.toUpperCase().substring(0, 1) + currentReplaceName.substring(1);					
					Method setMethod = valueObject.getClass().getMethod(setMethodName, java.lang.String.class);
					setMethod.invoke(valueObject, replaceMap.get(currentReplaceName));
				}
				isModified = true;
			}
			
			//전체삭제인지, 단수삭제인지 결정
			if(isAll != true) break;
		}
		
		if(isModified == true)
			VOHelper.writeStringToFile(file, VOHelper.toXML(xmlize, ec));
		
		if(isAtLeastOneMatch) return resultList;
		else return null;
	}
	
	/** EntryContainer 리턴 */
	public static EntryContainer getEntryContainer(String propName) throws IllegalArgumentException, IllegalAccessException {
		XMLize xmlize = VOHelper.getXMLize(propName);
		File file = VOHelper.getFile(propName);
		
		return 	(EntryContainer) xmlize.fromXML(file);
	}
	
	/** EntryContainer 리턴 */
	public static EntryContainer getEntryContainer(XMLize xmlize, File file) {
		return 	(EntryContainer) xmlize.fromXML(file);
	}
	
	/** EntryContainer -> XML */
	public static String toXML(XMLize xmlize, EntryContainer ec) throws Exception {
		return xmlize.toXML(ec);
	}
	
	/** Properties 키값으로 FILE 리턴 */
	public static File getFile(String propName) throws IllegalArgumentException, IllegalAccessException {
		return (File) extractField(propName, "FILE");
	}
	
	/** Properties 키값으로 XMLize 리턴 */
	public static XMLize getXMLize(String propName) throws IllegalArgumentException, IllegalAccessException {
		return (XMLize) extractField(propName, "XMLIZE");
	}
	
	/** 필드 반환 */
	public static Object extractField(String propName, String aSuffix) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = new VOHelper().getClass().getFields();
		
		Field targetField = null;
		
		for(int i=0; i<fields.length; i++) {
			Field tmpField = fields[i]; 
			
			String fieldName = tmpField.getName();
			int underBarIndex = fieldName.indexOf('_');
			String prefix = fieldName.substring(0, underBarIndex);
			String suffix = fieldName.substring(underBarIndex+1, fieldName.length());
			
			if(prefix.equals(propName) && suffix.equals(aSuffix)) {
				targetField = tmpField;
			}
		}
		return targetField.get(VOHelper.class);
	}
	
	/** 파일 입력 */
	public synchronized static void writeStringToFile(File file, String data) throws IOException {
		XMLCache.getInstance().clear(); //XML 로딩시 새로운 정보를 반영하게 하기 위함
		FileUtils.writeStringToFile(file, data);
	}
}