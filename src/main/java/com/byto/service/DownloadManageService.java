package com.byto.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.byto.observer.PackageObserver;
import com.byto.util.BytoUtils;
import com.byto.util.Sequence;
import com.byto.util.VOHelper;
import com.byto.util.XMLCache;
import com.byto.vo.Count;
import com.byto.vo.Download;
import com.byto.vo.Package;

@Component
public class DownloadManageService implements PackageObserver {

	private static Log log = LogFactory.getLog(DownloadManageService.class);
	private XMLCache xmlCache = XMLCache.getInstance();

	/** 다운로드 추가 */
	public void addDownload(String appCmd, String startHour,
			String endHour, String limitCount) throws Exception {
		if (Integer.valueOf(startHour) >= Integer.valueOf(endHour))
			throw new Exception("시작시간이 끝시간보다 크거나 같을 수 없습니다.");

		String packageSeq = ((Package) VOHelper.getVO("PACKAGE",
				"appCmd", appCmd)).getSeq();

		Download newDownload = new Download(Sequence.seq(), packageSeq,
				startHour, endHour, limitCount);
		hasDuplicateDownload(newDownload);

		VOHelper.addVO("DOWNLOAD", newDownload);
	}

	/** 동일한 DOWNLOAD가 있는지 검사 */
	public boolean hasDuplicateDownload(Download newDownload) throws Exception {
		List entries = VOHelper.getVOList("DOWNLOAD");

		int intStartHour = new Integer(newDownload.getStartHour());
		int intEndHour = new Integer(newDownload.getEndHour());

		Iterator<Download> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Download loopDown = iterator.next();
			if (!newDownload.getPackageSeq().equals(loopDown.getPackageSeq()))
				continue;

			int savedStartHour = new Integer(loopDown.getStartHour());
			int savedEndHour = new Integer(loopDown.getEndHour());

			int cnt = 0;
			for (int i = intStartHour; i < intEndHour; i++) {

				float compare = (float) (Float.valueOf(i) + 0.5);

				if (savedStartHour < compare && compare < savedEndHour) {
					throw new Exception("입력된 시간과 중복된 값이 있습니다.");
				}
			}
		}
		return false;
	}

	/** 다운로드 정보 리턴 */
	public List getDownloads(String appCmd) throws Exception {
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		String packageSeq = packageVO.getSeq();
		List entries = VOHelper.getVOList("DOWNLOAD");

		Iterator<Download> iterator = entries.iterator();
		List<Download> resultList = new ArrayList<Download>();

		while (iterator.hasNext()) {
			Download loopList = iterator.next();
			loopList.setAppCmd(appCmd); // 차후 수정

			if (!loopList.getPackageSeq().equals(packageSeq))
				continue;

			String zeroLeadingStartHour = BytoUtils.lpad('0',
					loopList.getStartHour(), 2);
			String zeroLeadingEndHour = BytoUtils.lpad('0',
					loopList.getEndHour(), 2);

			loopList.setStartHour(zeroLeadingStartHour);
			loopList.setEndHour(zeroLeadingEndHour);

			resultList.add(loopList);

		}
		return resultList;
	}

	/** 다운로드 삭제 */
	public void delDownload(String seq) throws Exception {
		VOHelper.delVO("DOWNLOAD", "seq", seq);
	}
	
	/** 패키지정보로 다운로드 삭제 */
	public void deleteByPackageSeq(String packageSeq) throws Exception {
		VOHelper.delVOs("DOWNLOAD", "packageSeq", packageSeq);
	}
}