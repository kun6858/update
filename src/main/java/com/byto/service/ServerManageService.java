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

	/** �űԼ��� ��� */
	public void addServer(String host, String port, String state) throws Exception {
		verifyServer(host); //������ ȣ��Ʈ�� �̹� �����ϴ��� Ȯ��
		VOHelper.addVO("SERVER", new Server(Sequence.seq(), host, port, state,
				BytoUtils.getSysdate()));
	}

	/** ��������Ʈ ���� */
	public List getServerList() throws Exception {
		return convertToReadableTimeFormat(VOHelper.getVOList("SERVER"));
	}
	
	/** ���н� Ÿ������ ����� ��¥�� �б⽬�����·� ��ȯ */
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
	
	/** ���� ȣ��Ʈ���� �̹� �����ϴ��� Ȯ�� */
	private void verifyServer(String host) throws Exception {
		verifyServer(null, host);
	}
	
	/** ���� ȣ��Ʈ���� �̹� �����ϴ��� Ȯ�� */
	private void verifyServer(String seq, String host) throws Exception {
		List serverList = VOHelper.getVOList("SERVER", "host", host);
		
		Iterator i = serverList.iterator();
		while(i.hasNext()) {
			Server serverVO = (Server) i.next();
			
			if(seq != null) {
				if(!serverVO.getSeq().equals(seq) && 
						serverVO.getHost().equals(host))
					throw new Exception("������ ����HOST�� �̹� �����մϴ�.");
			} else {
				if(serverVO.getHost().equals(host))
					throw new Exception("������ ����HOST�� �̹� �����մϴ�.");
			}
		}
		
	}
	
	/** ������������ ���� */
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

	/** ���� ���� */
	public void delServer(String seq) throws Exception {
		VOHelper.delVO("SERVER", "seq", seq);
		packageServerService.delPackageServersByServerSeq(seq); //��Ű�������� �Բ� ����
	}

	/** ���� ���� ���� */
	public void modServerState(String seq, String state) throws Exception {
		if ("0".equals(state))
			state = "1";
		else if ("1".equals(state))
			state = "0";

		VOHelper.modVO("SERVER", "seq", seq, "state", state);
	}
}
