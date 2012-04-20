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
	
	/** update XML �ʿ� ������ ���� */
	public synchronized Map getUpdateData(String cmd, HttpServletRequest request)	throws Exception {
		if (cmd == null) throw new Exception("cmd ����");
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
		
		//�������� ���� 
		serverVO = getServer(serverVOs);
		resultMap.put("server", serverVO);
		
		//�ٿ�ε� ���
		downloadVO = getDownload(downloadVOs);
		resultMap.put("download", downloadVO);
		
		//Count Validate
		if (packageVO.getUse().equals("1")) {
			processCount(downloadVO, packageVO.getSeq());
		}
		
		//Reserve ����
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
		
		//Request �߰�
		request.setAttribute("server", resultMap.get("server"));
		request.setAttribute("package", resultMap.get("package"));
		request.setAttribute("file", resultMap.get("file"));
		request.setAttribute("count", ((List) resultMap.get("file")).size());

		return resultMap;
	}
	
	/** ��Ű�� cmd�� ���� */
	public Package getPackageByAppCmd(String appCmd) throws Exception {
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		
		if(packageVO==null)
			throw new Exception("��Ű���� �������� �ʽ��ϴ�.");
		
		if(packageVO.getState().equals("0"))
			throw new Exception("��Ű�� ����� ������ �����Դϴ�.");
		
		return packageVO;
	}

	/** ��Ű�������� ��ϵ� ���� ����Ʈ ���� */
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
			throw new Exception("���뼭���� �����ϴ�.");
		
		return serverList;
	}
	
	/** ���� ����� ������ */
	public Server getServer(String serverSeq) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("state", "1"); //������¸� ����
		searchMap.put("seq", serverSeq);
		
		return (Server) VOHelper.getVO("SERVER", searchMap);
	}
	
	/** ���뼭���� �ϳ��� ���� ���� */
	private Server getServer(List serverList) throws Exception {
		Server server = (Server) serverList.get(BytoUtils.getRandomInt(0,
				serverList.size() - 1));
		return server;
	}

	/** �������� ����Ʈ ���� */
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

			//�������� ����Ȯ��
			String realState = fileService.isPhysicFileExist(
					pack.getSeq(), localFileName, buffer.getState());

			if (realState.equals("3")) {
				iterator.remove();
				continue;
			}

			//�±װ� ������� �����̸��� �±׸����λ��
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

			//������ ������
			buffer.setState(realState);
			buffer.setSize(FileManageService.getFileSize(pack.getSeq(),
					localFileName));
		}

		if (entries.size() == 0)
			throw new Exception("��ȿ�� ������ �����ϴ�.");

		return entries;
	}

	/** �������� ��� ���� */
	private List getReserveList(String packageSeq) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("state", "1");
		searchMap.put("packageSeq", packageSeq);
		
		return VOHelper.getVOList("RESERVE", searchMap);
	}

	/** �ٿ�ε� ��� ���� */
	private List getDownloadList(String packageSeq) throws Exception {
		return VOHelper.getVOList("DOWNLOAD", "packageSeq", packageSeq);
	}
	
	/** ���� �ð��� �ش��ϴ� Download ���� */
	private Download getDownload(List downloadList) {
		int currentHour = Integer.valueOf(DateFormatUtils.format(
				System.currentTimeMillis(), "HH"));

		Download targetDownload = null;

		//���� �ð��� �ش��ϴ� DOWNLOAD --> targetDownload
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

	/** �ٿ�ī��Ʈ ���� ó�� */
	private boolean processCount(Download targetDownload, String packageSeq)
			throws Exception {
		//����ð��� �ش��ϴ� DOWNLOAD ������ ���ܹ߻�
		if (targetDownload == null)
			throw new Exception("���� �ð����� �ٿ�ε带 �̿��Ͻ� �� �����ϴ�.");

		//�������� ��� �˻� ���� ���
		if (targetDownload.getLimitCount().equals("*"))
			return true;

		//�ִ� �ٿ�ε� Ƚ��
		int limitCount = Integer.valueOf(targetDownload.getLimitCount());
		
		//���� �ٿ�ε� Ƚ��
		int currentCount = getCountFromMemory(packageSeq,
				targetDownload.getStartHour(), targetDownload.getEndHour()); // ����ٿ�Ƚ��

		//�α�
		XMLogging.setLog("limitCount", String.valueOf(limitCount));
		XMLogging.setLog("currCount", String.valueOf(currentCount));

		//�ٿ�ε� Ƚ���˻�
		if (currentCount <= limitCount) {
			increaseCount(packageSeq, targetDownload.getSeq(),
					targetDownload.getStartHour(), targetDownload.getEndHour());
			return true; // �ٿ�ε� ����
		} else
			throw new Exception("�ٿ�ε� ����Ƚ���� �ʰ��Ǿ����ϴ�.");
	}

	/** �޸𸮷κ��� ���� �ٿ�ε� Ƚ���� ������ ������ 0�� ����  */
	private int getCountFromMemory(String packageSeq, String startHour, String endHour)
			throws Exception {
		Count targetCount = xmlCache.getCount(packageSeq, startHour, endHour);
		if (targetCount != null)
			return targetCount.getDownCount()+1;
		else
			return 1;
	}

	/** ���� �ٿ�ε� Ƚ�� ���� */
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

	/** ��ȿ���ϸ�Ͽ� Reserve ���� */
	public List applyReserve(List tmpFileList, List tmpReserveList,
			Package tmpPackage) throws Exception {
		Iterator fileIterator = tmpFileList.iterator();
		com.byto.vo.File tmpFile = null;

		while (fileIterator.hasNext()) {
			tmpFile = (com.byto.vo.File) fileIterator.next();

			//����ð� ���� �ڷ��� ������ �ֻ����ΰ��� ����
			getHighestVersion(tmpFile, tmpReserveList, tmpPackage);
		}

		return tmpFileList;
	}

	/** ������� ������ RESERVE���� */
	private Reserve getHighestVersion(com.byto.vo.File tmpFile,
			List tmpReserveList, Package tmpPackage) throws Exception {
		Iterator iterator = tmpReserveList.iterator();

		long currentDatetime = Long.valueOf(DateFormatUtils.format(
				System.currentTimeMillis(), "yyyyMMddHHmm"));

		Reserve savedReserve = null;

		while (iterator.hasNext()) {
			Reserve tmpReserve = (Reserve) iterator.next();

			//���Ͻ������� �����͸� �ش�
			if (!tmpReserve.getFileSeq().equals(tmpFile.getSeq()))
				continue;

			//applyTime�� ������� �ڵ��������
			if (tmpReserve.getApplyTime().equals(""))
				continue;

			//DB��¥�� ����ð����� �������
			if (currentDatetime < Long.valueOf(tmpReserve.getApplyTime()))
				continue;

			//���� ó����
			if (savedReserve == null) {
				savedReserve = tmpReserve;
				continue;
			}

			//���� ���� ������ ���Ե�
			if (Long.valueOf(tmpReserve.getVersion()) > Long
					.valueOf(savedReserve.getVersion()))
				savedReserve = tmpReserve;
		}

		//size�� ������
		if (savedReserve != null) {
			String FILEPATH = BytoUtils.getRidOfDoubleChar(
					"//",
					tmpPackage.getPath() + "/reserve/"
							+ savedReserve.getNewFilename());

			File file = new File(FILEPATH);

			if (!file.exists()) {
				return null; //������ ������ ���
			} else {
				tmpFile.setVersion(savedReserve.getVersion());
				tmpFile.setSize(String.valueOf(file.length()));
				tmpFile.setFinalFilename("/reserve/" + savedReserve.getNewFilename());
			}
		}
		return savedReserve;
	}

	/** ���� �ٿ�ε忡 �ش��ϴ� ����� �ʱ�ȭ */
	public void resetCount(String appCmd, String startHour, String endHour) throws Exception {
		List countList = xmlCache.getCounts();
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		Count count = xmlCache.getCount(packageVO.getSeq(), startHour, endHour);
		xmlCache.delCount(count);
	}
}