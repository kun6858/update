package com.byto.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.byto.observer.PackageObserver;
import com.byto.util.Sequence;
import com.byto.util.VOHelper;
import com.byto.vo.Package;
import com.byto.vo.PackageServer;
import com.byto.vo.Server;

@Component
public class PackageServerManageService implements PackageObserver {

	private static Log log = LogFactory
			.getLog(PackageServerManageService.class);

	/** 패키지 서버 정보 리턴 */
	public Map getPackageServers(String appCmd) throws Exception {
		Map map = new HashMap();
		map.put("appCmd", appCmd);
		map.put("regServer", getRegServers(appCmd));
		map.put("notRegServer",	getNotRegServers(appCmd, (List) map.get("regServer")));
		return map;
	}

	/** 등록서버 목록 리턴 */
	private List getRegServers(String appCmd) throws Exception {
		String packageSeq = ((Package) VOHelper.getVO("PACKAGE", "appCmd",
				appCmd)).getSeq();
		List<PackageServer> serverSeq = VOHelper.getVOList("PACKAGESERVER",
				"packageSeq", packageSeq);
		List serverList = new ArrayList();

		Iterator<PackageServer> iterator = serverSeq.iterator();

		while (iterator.hasNext()) {
			PackageServer tmpPackServ = iterator.next();
			Server tmpServer = (Server) VOHelper.getVO("SERVER", "seq",
					tmpPackServ.getServerSeq());
			serverList.add(tmpServer);
		}
		return serverList;
	}

	/** 미등록서버 목록 리턴 */
	public List getNotRegServers(String appCmd, List regServerList)
			throws Exception {
		List allServerList = VOHelper.getVOList("SERVER");

		Iterator iterator = allServerList.iterator();
		while (iterator.hasNext()) {
			Server tmpServer = (Server) iterator.next();

			if (isServerInPackageServer(appCmd, tmpServer, regServerList))
				iterator.remove();
		}

		return allServerList;
	}

	/** 서버가 패키지서버에 등록되었는지 확인 */
	public boolean isServerInPackageServer(String appCmd, Server server,
			List packageServerList) throws Exception {
		Iterator iterator = packageServerList.iterator();

		while (iterator.hasNext()) {
			Server tmpServer = (Server) iterator.next();
			if (tmpServer.getHost().equals(server.getHost()))
				return true;
		}
		return false;
	}

	/** 패키지서버 추가 */
	public void addPackageServer(String appCmd, String host) throws Exception {
		Server servVO = (Server) VOHelper.getVO("SERVER", "host", host);
		Package packVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);

		VOHelper.addVO("PACKAGESERVER", new PackageServer(Sequence.seq(),
				packVO.getSeq(), servVO.getSeq()));
	}

	/** 패키지서버 삭제 */
	public void delPackageServer(String appCmd, String host) throws Exception {
		Server servVO = (Server) VOHelper.getVO("SERVER", "host", host);
		Package packVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);

		Map searchMap = new HashMap();
		searchMap.put("serverSeq", servVO.getSeq());
		searchMap.put("packageSeq", packVO.getSeq());

		VOHelper.delVO("PACKAGESERVER", searchMap);
	}
	
	/** 패키지시퀀스로 패키지서버삭제 */
	public void deleteByPackageSeq(String packageSeq) throws Exception {
		VOHelper.delVOs("PACKAGESERVER", "packageSeq", packageSeq);
	}
	
	/** 서버가 삭제되면 패키지서버 목록도 함께 삭제 */
	public void delPackageServersByServerSeq(String serverSeq) throws Exception {
		VOHelper.delVOs("PACKAGESERVER", "serverSeq", serverSeq);
	}
}