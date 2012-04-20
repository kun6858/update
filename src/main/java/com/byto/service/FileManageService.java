package com.byto.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byto.fileupload.FileUpload;
import com.byto.observer.FileObserver;
import com.byto.observer.Observable;
import com.byto.observer.PackageObserver;
import com.byto.util.BytoUtils;
import com.byto.util.Sequence;
import com.byto.util.VOHelper;
import com.byto.vo.Package;
import com.oreilly.servlet.MultipartRequest;

@Component
public class FileManageService implements PackageObserver, Observable {

	private ArrayList observers;
	private Log log = LogFactory.getLog(FileManageService.class);
	public final String FILE_NOT_REGED = "2";
	public final String FILE_NOT_EXIST = "3";
	
	@Autowired ReserveManageService reserveService;
	@Autowired HttpServletRequest request;
	
	public FileManageService() {
		observers = new ArrayList();
		registerObserver(new ReserveManageService());
	}
	
	/** �űԹ������� */
	public String getVersion() {
		return DateFormatUtils.format(System.currentTimeMillis(),
				"yyyyMMddHHmmss");
	}

	/** �����߰� */
	public void addFile(String appCmd, String remoteFileName,
			String localFileName, String reg, String tagName, String type)
			throws Exception {
		Package packVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);

		VOHelper.addVO("FILE",
				new com.byto.vo.File(Sequence.seq(), packVO.getSeq(),
						remoteFileName, localFileName, getVersion(), reg, "1",
						tagName, type));
	}

	/** ���ϸ�ϻ��� */
	public JSONArray getFileList(String appCmd) throws Exception {
		Package packVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		String packageSeq = packVO.getSeq();
		
		List fileList = VOHelper.getVOList("FILE", "packageSeq", packageSeq);

		Iterator xmlIter = fileList.iterator();

		while (xmlIter.hasNext()) {
			com.byto.vo.File fileVO = (com.byto.vo.File) xmlIter.next();

			String localFileName = fileVO.getLocalFileName();

			fileVO.setState(isPhysicFileExist(packageSeq,
					localFileName, fileVO.getState()));
			fileVO.setSize(getFileSize(packageSeq, localFileName));

			if (fileVO.getType() == null)
				fileVO.setType(getFileExtension(packageSeq, localFileName));
		}

		File targetFolder = getTargetFolder(packageSeq);
		File[] fileArray = targetFolder.listFiles();

		if (fileArray == null)
			throw new Exception("��Ű�� ����(���)�� �������� �ʽ��ϴ�.");

		
		for (File file : fileArray) {
			if (file.isDirectory()) continue;
			if (isFilenameInList(fileList, file.getName())) continue;
			
			com.byto.vo.File fileVO = 
				new com.byto.vo.File(file.getName(), getFileExtension(file),
					FILE_NOT_REGED, String.valueOf(file.length()));
			fileList.add(fileVO);
		}

		return JSONArray.fromObject(fileList);
	}

	/** ���� ���� */
	public void delFile(String fileSeq) throws Exception {
		notifyObservers(fileSeq);
		// file db����
		VOHelper.delVO("FILE", "seq", fileSeq);
	}
	
	/** ��Ű���� �ش��ϴ� ���� ��ü ���� */
	public void deleteByPackageSeq(String packageSeq) throws Exception {
		VOHelper.delVOs("FILE", "packageSeq", packageSeq);
	}

	/** ���� ���� ���� */
	public void modFileState(String seq) throws Exception {
		com.byto.vo.File tmpFile = (com.byto.vo.File) VOHelper.getVO("FILE",
				"seq", seq);
		VOHelper.modVO("FILE", "seq", seq, "state",
				(tmpFile.getState().equals("0")) ? "1" : "0");
	}

	/** ���� ���� ���� */
	public void modFile(String seq, String remoteFileName, String reg,
			String tagName, String type, String version) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("seq", seq);

		Map replaceMap = new HashMap();
		replaceMap.put("remoteFileName", remoteFileName);
		replaceMap.put("reg", reg);
		replaceMap.put("tagName", tagName);
		replaceMap.put("type", type);
		replaceMap.put("version", version);

		VOHelper.modVO("FILE", searchMap, replaceMap);
	}

	/** ������ ���ϸ��� �̹� ����Ʈ�� �����ϴ��� �˻� */
	private boolean isFilenameInList(List fileList, String fileName) {
		Iterator i = fileList.iterator();
		while (i.hasNext()) {
			com.byto.vo.File tmpFile = (com.byto.vo.File) i.next();
			if (tmpFile.getLocalFileName().equals(fileName))
				return true;
		}
		return false;
	}

	/** ���� ������ �˻� */
	public static String getFileSize(String packageSeq, String localFileName)
			throws Exception {
		java.io.File targetFile = getTargetFile(packageSeq, localFileName);
		long fileSize = targetFile.length();
		return String.valueOf(fileSize);
	}

	/** ������ �ش��ϴ� File��ü ��ȯ */
	public java.io.File getTargetFolder(String packageSeq) throws Exception {
		return getTargetFile(packageSeq, "");
	}

	/** ���ϸ� �ش��ϴ� File��ü ��ȯ */
	public static java.io.File getTargetFile(String packageSeq,
			String localFileName) throws Exception {
		Package packVO = (Package) VOHelper.getVO("PACKAGE", "seq", packageSeq);
		String fullPath = packVO.getPath() + "/" + localFileName;
		return new java.io.File(fullPath);
	}

	/** ���� ���� ���� */
	public void modFileVersion(String seq) throws Exception {
		VOHelper.modVO("FILE", "seq", seq, "version", getVersion());
	}

	/** ����Ȯ���� ��ȯ */
	public String getFileExtension(File file) {
		java.io.File targetFile = file;

		int index = targetFile.getName().lastIndexOf('.');
		if (index > 0 && index <= targetFile.getName().length() - 2) {
			return targetFile.getName().substring(index + 1);
		}

		return "";
	}

	/** ����Ȯ���� ��ȯ */
	public String getFileExtension(String packageSeq, String localFileName)
			throws Exception {
		java.io.File targetFile = getTargetFile(packageSeq, localFileName);

		int index = targetFile.getName().lastIndexOf('.');
		if (index > 0 && index <= targetFile.getName().length() - 2) {
			return targetFile.getName().substring(index + 1);
		}

		return "";
	}

	/** ���������� �ִ��� �˻��ϸ� ���� ����� 0 �Ǵ� 1 ���� ������ 3 ��ȯ */
	public String isPhysicFileExist(String packageSeq,
			String localFileName, String state) throws Exception {
		Package pack = (Package) VOHelper.getVO("PACKAGE", "seq", packageSeq);
		String fullPath = pack.getPath() + "/" + localFileName;

		File tmpFile = new java.io.File(fullPath);
		if (!tmpFile.exists())
			return FILE_NOT_EXIST;
		else
			return state;
	}

	/** ���� ���ε� */
	public void uploadFile(String appCmd) throws Exception {
		final String UPLOAD_FULLPATH = ((Package) VOHelper.getVO("PACKAGE",
				"appCmd", appCmd)).getPath();

		if (UPLOAD_FULLPATH.equals("") || UPLOAD_FULLPATH == null)
			throw new Exception("���� ���ε�� ������ �������� �ʽ��ϴ�.");

		MultipartRequest multiPartRequest = FileUpload.upload(request, UPLOAD_FULLPATH);
	}

	/** �������� ���� */
	public void delPhysicFile(String appCmd, String localFileName)
			throws Exception {
		
		final String UPLOAD_FULLPATH = ((Package) VOHelper.getVO("PACKAGE",
				"appCmd", appCmd)).getPath();

		if (UPLOAD_FULLPATH.equals("") || UPLOAD_FULLPATH == null)
			throw new Exception("���� ���ε�� ������ �������� �ʽ��ϴ�.");

		String filepath = UPLOAD_FULLPATH + "/" + localFileName;
		filepath = BytoUtils.getRidOfDoubleChar("//", filepath);

		if (!new File(filepath).delete()) {
			throw new Exception("���� ������ �����߽��ϴ�.");
		}
	}

	public void registerObserver(Object o) {
		observers.add(o);
	}

	public void notifyObservers(String fileSeq) throws Exception {
		for(int i = 0; i < observers.size(); i++) {
			FileObserver file = (FileObserver) observers.get(i);
			file.deleteByFileSeq(fileSeq);
		}
	}
}