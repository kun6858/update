package com.byto.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.byto.observer.PackageObserver;
import com.byto.observer.Observable;
import com.byto.util.BytoUtils;
import com.byto.util.Sequence;
import com.byto.util.VOHelper;
import com.byto.vo.Package;

@Component
public class PackageManageService implements Observable {
	
	private ArrayList observers;
	private Log log = LogFactory.getLog(PackageManageService.class);
	
	public PackageManageService () {
		observers = new ArrayList();
		registerObserver(new DownloadManageService());
		registerObserver(new FileManageService());
		registerObserver(new ReserveManageService());
		registerObserver(new PackageServerManageService());
	}

	/** 패키지목록 리턴 */
	public List getPackageList() throws Exception {
		return VOHelper.getVOList("PACKAGE");
	}

	/** 패키지 경로가 존재하는지 검사 */
	private void isPhysicPathExist(String path) throws Exception {
		File pathFile = new File(path);
		if (!pathFile.exists())
			throw new Exception("실제 경로(Path)가 존재하지 않습니다.");
	}
	
	/** 동일한 Path가 존재하는지 검사 */
	private void hasDuplicatePath(String packageSeq, String path) throws Exception {
		List entries = getPackageList();
		
		Iterator<Package> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Package buffer = iterator.next();

			if (!buffer.getSeq().equals(packageSeq) && 
					buffer.getPath().equals(path)) {
				throw new Exception("이미 존재하는 경로입니다");
			}
		}
	}

	/** 같은 appCmd가 존재하는지 검사 */
	public void isAppCmdExist(String appCmd) throws Exception {
		isAppCmdExist(appCmd, null);
	}

	/** 같은 appCmd가 존재하는지 검사 */
	public void isAppCmdExist(String appCmd, String packageSeq)
			throws Exception {
		List entries = getPackageList();

		Iterator<Package> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Package buffer = iterator.next();

			if (!buffer.getSeq().equals(packageSeq) && 
					buffer.getAppCmd().equals(appCmd)) {
				throw new Exception("동일한 App Cmd가 존재합니다.");
			}
		}
	}

	/** 패키지 추가 */
	public void addPackage(String appName, String appCmd, String path,
			String state, String memo, String use, String remotePrefix)
			throws Exception {
		isPhysicPathExist(path);
		hasDuplicatePath(null, path);
		isAppCmdExist(appCmd);

		String version = DateFormatUtils.format(System.currentTimeMillis(),
				"yyyyMMdd") + "01";

		VOHelper.addVO("PACKAGE", new Package(Sequence.seq(), appName, appCmd,
				version, path, state, memo, use, remotePrefix));
	}

	/** 패키지 삭제 */
	public void delPackage(String packageSeq) throws Exception {
		//Observer 알림
		notifyObservers(packageSeq);
		
		//PACKAGE 삭제
		VOHelper.delVO("PACKAGE", "seq", packageSeq);
	}

	/** 패키지 상태 변경 */
	public void modPackageState(String seq) throws Exception {
		String state = ((Package) VOHelper.getVO("PACKAGE", "seq", seq))
				.getState();

		if ("0".equals(state))
			state = "1";
		else if ("1".equals(state))
			state = "0";

		VOHelper.modVO("PACKAGE", "seq", seq, "state", state);
	}

	/** 패키지 시간 사용 상태변경 */
	public void modPackageUse(String seq) throws Exception {
		String use = ((Package) VOHelper.getVO("PACKAGE", "seq", seq)).getUse();

		if ("0".equals(use))
			use = "1";
		else if ("1".equals(use))
			use = "0";

		VOHelper.modVO("PACKAGE", "seq", seq, "use", use);
	}

	/** 패키지 설정 변경 */
	public void modPackage(String seq, String appName, String appCmd,
			String path, String memo, String remotePrefix, String version)
			throws Exception {
		isPhysicPathExist(path);
		isAppCmdExist(appCmd, seq);
		hasDuplicatePath(seq, path);

		Map searchMap = new HashMap();
		searchMap.put("seq", seq);

		Map replaceMap = new HashMap();
		replaceMap.put("appName", appName);
		replaceMap.put("appCmd", appCmd);
		replaceMap.put("path", path);
		replaceMap.put("memo", memo);
		replaceMap.put("remotePrefix", remotePrefix);
		replaceMap.put("version", version);

		VOHelper.modVO("PACKAGE", searchMap, replaceMap);
	}

	/** 패키지 시퀀스로 리턴 */
	public Package getPackageBySeq(String packageSeq) throws Exception {
		return (Package) VOHelper.getVO("PACKAGE", "seq", packageSeq);
	}
	
	/** 패키지 버젼 증가 */
	public String increseVersion(String oldVersion) {
		String currentDate = oldVersion.substring(0, 8);
		String currentVersion = oldVersion.substring(8);
		String newVersion = BytoUtils.putZero(String.valueOf(Integer
				.valueOf(currentVersion) + 1));

		String today = DateFormatUtils.format(System.currentTimeMillis(),
				"yyyyMMdd");

		if (today.equals(currentDate)) {
			return currentDate + newVersion;
		} else {
			return today + "01";
		}
	}

	/** 패키지 버젼 수정 */
	public void modPackageVersion(String packageSeq) throws Exception {
		String oldVersion = 
			((Package) VOHelper.getVO("PACKAGE", "seq", packageSeq)).getVersion();
		
		VOHelper.modVO("PACKAGE", "seq", 
				packageSeq, "version", increseVersion(oldVersion));
	}
	
	/** appName을 반환 */
	public List getAppNames() throws Exception {
		List packList = VOHelper.getVOList("PACKAGE");
		List appNameList = new ArrayList();
		Iterator<Package> iterator = packList.iterator();
		Set retSet = new HashSet();

		while (iterator.hasNext()) {
			Package pack = iterator.next();
			retSet.add(pack.getAppName());
		}
		appNameList.addAll(retSet);
		return appNameList;
	}
	
	/** appCmd을 반환 */
	public List getAppCmds(String appName) throws Exception {
		List packList = VOHelper.getVOList("PACKAGE");

		Iterator<Package> iterator = packList.iterator();

		List returnList = new ArrayList();

		while (iterator.hasNext()) {
			Package pack = iterator.next();
			if (pack.getAppName().equals(appName)) {
				returnList.add(pack.getAppCmd());
			}
		}
		return returnList;
	}
	
	/** 패키지 메모를 호출 */
	public Map getPackageMemo() throws Exception {
		List packList = VOHelper.getVOList("PACKAGE");

		Iterator<Package> iterator = packList.iterator();
		Map retMap = new HashMap();

		while (iterator.hasNext()) {
			Package pack = iterator.next();
			retMap.put(pack.getAppCmd(), pack.getMemo());
		}
		return retMap;
	}

	public void registerObserver(Object o) {
		observers.add(o);
	}

	public void notifyObservers(String packageSeq) throws Exception {
		for(int i=0; i < observers.size(); i++) {
			PackageObserver observer = (PackageObserver) observers.get(i);
			observer.deleteByPackageSeq(packageSeq);
		}
	}
	
	/** 패키지정보를 JSON형태로 반환 */
	public JSONArray getPackageInfomationInJson() throws Exception {
		List packageList = new ArrayList();
		List appNameList = getAppNames();

		Iterator loop = appNameList.iterator();

		while (loop.hasNext()) {
			Map tmpMap = new HashMap();
			String tmpAppName = (String) loop.next();
			tmpMap.put("appName", tmpAppName);
			tmpMap.put("appCmd", getAppCmds(tmpAppName));
			packageList.add(tmpMap);
		}

		Map map = new HashMap();
		map.put("memo", getPackageMemo());
		packageList.add(map);

		return JSONArray.fromObject(packageList);
	}
}