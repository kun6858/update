package com.byto.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byto.util.BytoUtils;
import com.byto.util.VOHelper;
import com.byto.util.XMLCache;
import com.byto.util.XMLogging;
import com.byto.vo.Count;
import com.byto.vo.Download;
import com.byto.vo.Package;
import com.byto.vo.PackageServer;
import com.byto.vo.Reserve;
import com.byto.vo.Server;

@Component
public class UpdateXmlService {

	private Log log = LogFactory.getLog(this.getClass());

	@Autowired private HttpServletRequest request;
	@Autowired private FileManageService fileService;
	
	private XMLCache xmlCache = XMLCache.getInstance();
	
	/** update XML 필요 데이터 리턴 */
	public synchronized Map getUpdateData(String cmd, HttpServletRequest request)	throws Exception {
		if (cmd == null) throw new Exception("cmd 누락");
		Map resultMap = xmlCache.getCache(cmd);
		
		Package packageVO = null;
		List serverVOs = null;
		Server serverVO = null;
		List fileVOs = null;
		List reserveVOs = null;
		List downloadVOs = null;
		Download downloadVO = null;

		if (resultMap.size() == 0) {
			packageVO = getPackageByAppCmd(cmd);
			serverVOs = getServerList(packageVO.getSeq());
			fileVOs = getFileList(packageVO);
			reserveVOs = getReserveList(packageVO.getSeq());
			downloadVOs = getDownloadList(packageVO.getSeq());

			resultMap.put("package", packageVO);
			resultMap.put("serverList", serverVOs);
			resultMap.put("file", fileVOs);
			resultMap.put("server", serverVO);
			resultMap.put("reserve", reserveVOs);
			resultMap.put("downloadList", downloadVOs);

			xmlCache.setCache(cmd, resultMap);
		} else {
			packageVO = (Package) resultMap.get("package");
			serverVOs = (List) resultMap.get("serverList");
			fileVOs = (List) resultMap.get("file");
			reserveVOs = (List) resultMap.get("reserve");
			downloadVOs = (List) resultMap.get("downloadList");
		}
		
		//랜덤서버 선택 
		serverVO = getServer(serverVOs);
		resultMap.put("server", serverVO);
		
		//다운로드 등록
		downloadVO = getDownload(downloadVOs);
		resultMap.put("download", downloadVO);
		
		//Count Validate
		if (packageVO.getUse().equals("1")) {
			processCount(downloadVO, packageVO.getSeq());
		}
		
		//Reserve 적용
		resultMap.put("file", applyReserve(fileVOs, reserveVOs, packageVO));
		
		//Log
		XMLogging.setLog("packName", packageVO.getAppName());
		XMLogging.setLog("packCmd", packageVO.getAppCmd());
		XMLogging.setLog("packMemo", packageVO.getMemo());	
		XMLogging.setLog("hourUsable", packageVO.getUse());
		XMLogging.setLog("responseIP", serverVO.getHost());
		XMLogging.setLog("responsePort", serverVO.getPort());
		XMLogging.setLog("currTime", new Date().toString());
		
		if(packageVO.getUse().equals("1")) {
			XMLogging.setLog("startHour", downloadVO.getStartHour());
			XMLogging.setLog("endHour", downloadVO.getEndHour());
		}
		
		XMLogging.setLog("currHour", DateFormatUtils.format(System.currentTimeMillis(), "HH"));
		
		//Request 추가
		request.setAttribute("server", resultMap.get("server"));
		request.setAttribute("package", resultMap.get("package"));
		request.setAttribute("file", resultMap.get("file"));
		request.setAttribute("count", ((List) resultMap.get("file")).size());

		return resultMap;
	}
	
	/** 패키지 cmd로 리턴 */
	public Package getPackageByAppCmd(String appCmd) throws Exception {
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		
		if(packageVO==null)
			throw new Exception("패키지가 존재하지 않습니다.");
		
		if(packageVO.getState().equals("0"))
			throw new Exception("패키지 사용이 중지된 상태입니다.");
		
		return packageVO;
	}

	/** 패키지서버에 등록된 서버 리스트 리턴 */
	private List getServerList(String packageSeq) throws Exception {
		List packServList = (List) VOHelper.getVOList("PACKAGESERVER",
				"packageSeq", packageSeq);
		List<Server> serverList = new ArrayList<Server>();

		Iterator i = packServList.iterator();

		while (i.hasNext()) {
			PackageServer tmpPackServ = (PackageServer) i.next();
			String serverSeq = tmpPackServ.getServerSeq();

			Server tmpServer = getServer(serverSeq);

			if (tmpServer != null)
				serverList.add(tmpServer);
		}
		
		if(serverList == null || serverList.size() == 0)
			throw new Exception("가용서버가 없습니다.");
		
		return serverList;
	}
	
	/** 서버 목록을 가져옮 */
	public Server getServer(String serverSeq) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("state", "1"); //정상상태만 추출
		searchMap.put("seq", serverSeq);
		
		return (Server) VOHelper.getVO("SERVER", searchMap);
	}
	
	/** 가용서버중 하나를 랜덤 선택 */
	private Server getServer(List serverList) throws Exception {
		Server server = (Server) serverList.get(BytoUtils.getRandomInt(0,
				serverList.size() - 1));
		return server;
	}

	/** 가용파일 리스트 리턴 */
	private List getFileList(Package pack) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("packageSeq", pack.getSeq());
		searchMap.put("state", "1");

		List entries = VOHelper.getVOList("FILE", searchMap);

		Iterator iterator = entries.iterator();
		com.byto.vo.File buffer = null;
		
		while (iterator.hasNext()) {
			buffer = (com.byto.vo.File) iterator.next();
			String localFileName = buffer.getLocalFileName();
			String remoteFileName = buffer.getRemoteFileName();
			String state = buffer.getState();
			String tagName = buffer.getTagName();

			//물리파일 존재확인
			String realState = fileService.isPhysicFileExist(
					pack.getSeq(), localFileName, buffer.getState());

			if (realState.equals("3")) {
				iterator.remove();
				continue;
			}

			//태그가 비었으면 파일이름을 태그명으로사용
			if ("".equals(tagName))
				buffer.setTagName(buffer.getFilename());

			StringBuilder remote = new StringBuilder();
			remote.append(request.getContextPath());
			remote.append("/");
			
			if (!"".equals(pack.getRemotePrefix()))
				remote.append(pack.getRemotePrefix());
			
			remote.append("/");				
			
			String finalFilename = "";
			if (!"".equals(remoteFileName))
				finalFilename = remoteFileName;
			else
				finalFilename = localFileName;
			
			buffer.setRemote(remote.toString());
			buffer.setFinalFilename(finalFilename);

			//부족한 데이터
			buffer.setState(realState);
			buffer.setSize(FileManageService.getFileSize(pack.getSeq(),
					localFileName));
		}

		if (entries.size() == 0)
			throw new Exception("유효한 파일이 없습니다.");

		return entries;
	}

	/** 예약파일 목록 리턴 */
	private List getReserveList(String packageSeq) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("state", "1");
		searchMap.put("packageSeq", packageSeq);
		
		return VOHelper.getVOList("RESERVE", searchMap);
	}

	/** 다운로드 목록 리턴 */
	private List getDownloadList(String packageSeq) throws Exception {
		return VOHelper.getVOList("DOWNLOAD", "packageSeq", packageSeq);
	}
	
	/** 현재 시간에 해당하는 Download 리턴 */
	private Download getDownload(List downloadList) {
		int currentHour = Integer.valueOf(DateFormatUtils.format(
				System.currentTimeMillis(), "HH"));

		Download targetDownload = null;

		//현재 시간에 해당하는 DOWNLOAD --> targetDownload
		Iterator<Download> iterator = downloadList.iterator();
		while (iterator.hasNext()) {
			Download tmpDownload = iterator.next();

			int savedStartHour = new Integer(tmpDownload.getStartHour());
			int savedEndHour = new Integer(tmpDownload.getEndHour());

			if ((savedStartHour <= currentHour && currentHour < savedEndHour)) {
				targetDownload = tmpDownload;
				break;
			}
		}
		return targetDownload;
	}

	/** 다운카운트 증가 처리 */
	private boolean processCount(Download targetDownload, String packageSeq)
			throws Exception {
		//현재시간에 해당하는 DOWNLOAD 없으면 예외발생
		if (targetDownload == null)
			throw new Exception("현재 시간에는 다운로드를 이용하실 수 없습니다.");

		//무제한일 경우 검사 강제 통과
		if (targetDownload.getLimitCount().equals("*"))
			return true;

		//최대 다운로드 횟수
		int limitCount = Integer.valueOf(targetDownload.getLimitCount());
		
		//현재 다운로드 횟수
		int currentCount = getCountFromMemory(packageSeq,
				targetDownload.getStartHour(), targetDownload.getEndHour()); // 현재다운횟수

		//로그
		XMLogging.setLog("limitCount", String.valueOf(limitCount));
		XMLogging.setLog("currCount", String.valueOf(currentCount));

		//다운로드 횟수검사
		if (currentCount <= limitCount) {
			increaseCount(packageSeq, targetDownload.getSeq(),
					targetDownload.getStartHour(), targetDownload.getEndHour());
			return true; // 다운로드 가능
		} else
			throw new Exception("다운로드 제한횟수가 초과되었습니다.");
	}

	/** 메모리로부터 현재 다운로드 횟수를 얻어오며 없을시 0을 리턴  */
	private int getCountFromMemory(String packageSeq, String startHour, String endHour)
			throws Exception {
		Count targetCount = xmlCache.getCount(packageSeq, startHour, endHour);
		if (targetCount != null)
			return targetCount.getDownCount()+1;
		else
			return 1;
	}

	/** 현재 다운로드 횟수 증가 */
	private void increaseCount(String packageSeq, String downSeq,
			String startHour, String endHour) throws Exception {
		Count targetCount = xmlCache.getCount(packageSeq, startHour, endHour);

		if (targetCount != null)
			targetCount.setDownCount(targetCount.getDownCount() + 1);
		else {
			Count count = new Count(packageSeq, downSeq, startHour, endHour, 1);
			xmlCache.addCount(count);
		}
	}

	/** 유효파일목록에 Reserve 적용 */
	public List applyReserve(List tmpFileList, List tmpReserveList,
			Package tmpPackage) throws Exception {
		Iterator fileIterator = tmpFileList.iterator();
		com.byto.vo.File tmpFile = null;

		while (fileIterator.hasNext()) {
			tmpFile = (com.byto.vo.File) fileIterator.next();

			//현재시각 이후 자료중 버젼이 최상위인것을 리턴
			getHighestVersion(tmpFile, tmpReserveList, tmpPackage);
		}

		return tmpFileList;
	}

	/** 가장높은 버젼의 RESERVE선택 */
	private Reserve getHighestVersion(com.byto.vo.File tmpFile,
			List tmpReserveList, Package tmpPackage) throws Exception {
		Iterator iterator = tmpReserveList.iterator();

		long currentDatetime = Long.valueOf(DateFormatUtils.format(
				System.currentTimeMillis(), "yyyyMMddHHmm"));

		Reserve savedReserve = null;

		while (iterator.hasNext()) {
			Reserve tmpReserve = (Reserve) iterator.next();

			//파일시퀀스랑 같은것만 해당
			if (!tmpReserve.getFileSeq().equals(tmpFile.getSeq()))
				continue;

			//applyTime이 비었으면 자동적용안함
			if (tmpReserve.getApplyTime().equals(""))
				continue;

			//DB날짜가 현재시간보다 작을경우
			if (currentDatetime < Long.valueOf(tmpReserve.getApplyTime()))
				continue;

			//제일 처음비교
			if (savedReserve == null) {
				savedReserve = tmpReserve;
				continue;
			}

			//제일 높은 버젼만 남게됨
			if (Long.valueOf(tmpReserve.getVersion()) > Long
					.valueOf(savedReserve.getVersion()))
				savedReserve = tmpReserve;
		}

		//size를 재정의
		if (savedReserve != null) {
			String FILEPATH = BytoUtils.getRidOfDoubleChar(
					"//",
					tmpPackage.getPath() + "/reserve/"
							+ savedReserve.getNewFilename());

			File file = new File(FILEPATH);

			if (!file.exists()) {
				return null; //파일이 삭제된 경우
			} else {
				tmpFile.setVersion(savedReserve.getVersion());
				tmpFile.setSize(String.valueOf(file.length()));
				tmpFile.setFinalFilename("/reserve/" + savedReserve.getNewFilename());
			}
		}
		return savedReserve;
	}

	/** 현재 다운로드에 해당하는 기록을 초기화 */
	public void resetCount(String appCmd, String startHour, String endHour) throws Exception {
		List countList = xmlCache.getCounts();
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		Count count = xmlCache.getCount(packageVO.getSeq(), startHour, endHour);
		xmlCache.delCount(count);
	}
}