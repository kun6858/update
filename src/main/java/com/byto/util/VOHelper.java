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
 * XML���Ͽ� ValueObject�� ����ϱ� ���ؼ� �ʿ��� CRUD �۾��� �����ϴ� Ŭ����
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
	 * �ű� ValueObject�� ���Ͽ� �߰�
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

	/** �ܼ� ���� */
	public static void delVO(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		VOHelper.processVO(propName, searchMap, null, false, "delete"); //false means one!! 
	}
	
	/** �ܼ� ���� */
	public static void delVO(String propName, Map searchMap) throws Exception {
		VOHelper.processVO(propName, searchMap, null, false, "delete"); //false means one!! 
	}
	
	/** ���� ���� */
	public static void delVOs(String propName, Map searchMap) throws Exception {
		VOHelper.processVO(propName, searchMap, null, true, "delete");
	}
	
	/** ���� ���� */
	public static void delVOs(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		VOHelper.processVO(propName, searchMap, null, true, "delete");
	}
	
	/** �ܼ�ġȯ */
	public static void modVO(String propName, String searchKey, String searchVal, String replaceKey, String replaceVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		Map replaceMap = new HashMap();
		replaceMap.put(replaceKey, replaceVal);
		VOHelper.processVO(propName, searchMap, replaceMap, false, "modify");
	}
	
	/** �ܼ�ġȯ */
	public static void modVO(String propName, Map searchMap, Map replaceMap) throws Exception {
		VOHelper.processVO(propName, searchMap, replaceMap, false, "modify");
	}
	
	/** ����ġȯ */
	public static void modVOs(String propName, String searchKey, String searchVal, String replaceKey, String replaceVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		Map replaceMap = new HashMap();
		replaceMap.put(replaceKey, replaceVal);
		VOHelper.processVO(propName, searchMap, replaceMap, true, "modify");
	}
	
	/** ����ġȯ */
	public static void modVOs(String propName, Map searchMap, Map replaceMap) throws Exception {
		VOHelper.processVO(propName, searchMap, replaceMap, true, "modify");
	}
	
	/** �ܼ� �б� */
	public static Object getVO(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		
		return processVO(propName, searchMap, null, false, "read");
	}
	
	/** �ܼ� �б� */
	public static Object getVO(String propName, Map<String, String> searchMap) throws Exception {
		return (Object) processVO(propName, searchMap, null, false, "read");
	}

	/** ���� �б� */
	public static List getVOList(String propName) throws Exception {
		XMLize xmlize = VOHelper.getXMLize(propName);
		File file = VOHelper.getFile(propName);
		return VOHelper.getEntryContainer(xmlize, file).getEntries();
	}
	
	/** �����б� */
	public static List getVOList(String propName, Map searchMap) throws Exception {
		List list = (List) VOHelper.processVO(propName, searchMap, null, true, "read");
		if(list == null) return new ArrayList();
		return list;
	}
	
	/** �����б� */
	public static List getVOList(String propName, String searchKey, String searchVal) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put(searchKey, searchVal);
		
		return (List) VOHelper.getVOList(propName, searchMap);
	}
	
	/** VO �б�, ���� �Ǵ� ���� ó�� */ 
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
			
			//��Ī�� ������ Ʋ���� �ߴ�
			if(matchedSize != searchSize) continue; 
			else isAtLeastOneMatch = true; 
			
			//�б����ϰ�� VO����
			if("read".equals(mode)) {
				if(isAll == true)
					resultList.add(valueObject);
				else
					return (Object) valueObject;
			}
			
			//��������ϰ�� VO����
			if("delete".equals(mode)) {
				//��������
				iterator.remove();
				isModified = true;
			}

			//��������ϰ�� VO����
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
			
			//��ü��������, �ܼ��������� ����
			if(isAll != true) break;
		}
		
		if(isModified == true)
			VOHelper.writeStringToFile(file, VOHelper.toXML(xmlize, ec));
		
		if(isAtLeastOneMatch) return resultList;
		else return null;
	}
	
	/** EntryContainer ���� */
	public static EntryContainer getEntryContainer(String propName) throws IllegalArgumentException, IllegalAccessException {
		XMLize xmlize = VOHelper.getXMLize(propName);
		File file = VOHelper.getFile(propName);
		
		return 	(EntryContainer) xmlize.fromXML(file);
	}
	
	/** EntryContainer ���� */
	public static EntryContainer getEntryContainer(XMLize xmlize, File file) {
		return 	(EntryContainer) xmlize.fromXML(file);
	}
	
	/** EntryContainer -> XML */
	public static String toXML(XMLize xmlize, EntryContainer ec) throws Exception {
		return xmlize.toXML(ec);
	}
	
	/** Properties Ű������ FILE ���� */
	public static File getFile(String propName) throws IllegalArgumentException, IllegalAccessException {
		return (File) extractField(propName, "FILE");
	}
	
	/** Properties Ű������ XMLize ���� */
	public static XMLize getXMLize(String propName) throws IllegalArgumentException, IllegalAccessException {
		return (XMLize) extractField(propName, "XMLIZE");
	}
	
	/** �ʵ� ��ȯ */
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
	
	/** ���� �Է� */
	public synchronized static void writeStringToFile(File file, String data) throws IOException {
		XMLCache.getInstance().clear(); //XML �ε��� ���ο� ������ �ݿ��ϰ� �ϱ� ����
		FileUtils.writeStringToFile(file, data);
	}
}