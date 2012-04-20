package com.byto.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byto.util.BytoUtils;
import com.byto.util.Sequence;
import com.byto.util.VOHelper;
import com.byto.vo.Server;

@Component
public class ServerManageService {

	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired PackageServerManageService packageServerService;

	/** 신규서버 등록 */
	public void addServer(String host, String port, String state) throws Exception {
		verifyServer(host); //동일한 호스트가 이미 존재하는지 확인
		VOHelper.addVO("SERVER", new Server(Sequence.seq(), host, port, state,
				BytoUtils.getSysdate()));
	}

	/** 서버리스트 리턴 */
	public List getServerList() throws Exception {
		return convertToReadableTimeFormat(VOHelper.getVOList("SERVER"));
	}
	
	/** 유닉스 타임으로 저장된 날짜를 읽기쉬운형태로 변환 */
	public List convertToReadableTimeFormat(List list) {
		Iterator i = list.iterator();
		while(i.hasNext()) {
			Server server = (Server) i.next();
			String unixtime = server.getRegDate();
			String readableTime = BytoUtils.DateFormat(unixtime, "yyyy-MM-dd hh:mm:ss");
			server.setRegDate(readableTime);
		}
		return list;
	}
	
	/** 같은 호스트명이 이미 존재하는지 확인 */
	private void verifyServer(String host) throws Exception {
		verifyServer(null, host);
	}
	
	/** 같은 호스트명이 이미 존재하는지 확인 */
	private void verifyServer(String seq, String host) throws Exception {
		List serverList = VOHelper.getVOList("SERVER", "host", host);
		
		Iterator i = serverList.iterator();
		while(i.hasNext()) {
			Server serverVO = (Server) i.next();
			
			if(seq != null) {
				if(!serverVO.getSeq().equals(seq) && 
						serverVO.getHost().equals(host))
					throw new Exception("동일한 서버HOST가 이미 존재합니다.");
			} else {
				if(serverVO.getHost().equals(host))
					throw new Exception("동일한 서버HOST가 이미 존재합니다.");
			}
		}
		
	}
	
	/** 서버설정정보 변경 */
	public void modServer(String seq, String host, String port)
			throws Exception {
		verifyServer(seq, host);
		
		Map searchMap = new HashMap();
		searchMap.put("seq", seq);

		Map replaceMap = new HashMap();
		replaceMap.put("host", host);
		replaceMap.put("port", port);

		VOHelper.modVO("SERVER", searchMap, replaceMap);
	}

	/** 서버 삭제 */
	public void delServer(String seq) throws Exception {
		VOHelper.delVO("SERVER", "seq", seq);
		packageServerService.delPackageServersByServerSeq(seq); //패키지서버도 함께 삭제
	}

	/** 서버 상태 변경 */
	public void modServerState(String seq, String state) throws Exception {
		if ("0".equals(state))
			state = "1";
		else if ("1".equals(state))
			state = "0";

		VOHelper.modVO("SERVER", "seq", seq, "state", state);
	}
}
