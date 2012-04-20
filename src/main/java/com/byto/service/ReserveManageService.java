package com.byto.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byto.fileupload.FileUpload;
import com.byto.observer.FileObserver;
import com.byto.observer.PackageObserver;
import com.byto.util.BytoUtils;
import com.byto.util.VOHelper;
import com.byto.vo.Package;
import com.byto.vo.Reserve;
import com.oreilly.servlet.MultipartRequest;

@Component
public class ReserveManageService implements PackageObserver, FileObserver {

	private Log log = LogFactory.getLog(this.getClass());
	
	@Autowired private FileManageService fileService; 
	@Autowired private HttpServletRequest request;
	@Autowired private ServletContext servletContext;

	/** 예약폴더 FILE객체 리턴 */
	public java.io.File getReserveFolder(Package packVO) throws Exception {
		String reserveFolderPath = packVO.getPath() + "/reserve/";
		return new java.io.File(reserveFolderPath);
	}

	/** 예약목록 리턴 */
	public JSONArray getReserveList(String appCmd, String fileSeq)
			throws Exception {
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd",
				appCmd);
		com.byto.vo.File fileVO = (com.byto.vo.File) VOHelper.getVO("FILE",
				"seq", fileSeq);

		List reserveList = VOHelper.getVOList("RESERVE", "fileSeq", fileSeq);

		String FOLDER = BytoUtils.getRidOfDoubleChar("//", packageVO.getPath()
				+ "/reserve/");

		Iterator resIterator = reserveList.iterator();
		
		while (resIterator.hasNext()) {
			Reserve tmpReserve = (Reserve) resIterator.next();

			// 파일 실제 존재여부 검사
			File file = new File(FOLDER + tmpReserve.getNewFilename());
			String state = null;
			String size = null;

			if (file.exists() == false) {
				state = fileService.FILE_NOT_EXIST;
			} else {
				state = tmpReserve.getState();
				size = BytoUtils.toCommifyString(String.valueOf(file.length()));
			}

			tmpReserve.setState(state);
			tmpReserve.setSize(size);
		}

		return JSONArray.fromObject(reserveList);
	}

	/** 예약파일업로드 */
	public void addReserve(String appCmd, String fileSeq) throws Exception {
		Package packageVO = 
				(Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd);
		
		com.byto.vo.File fileVO = 
				(com.byto.vo.File) VOHelper.getVO("FILE", "seq", fileSeq);

		String UPLOAD_FULLPATH = BytoUtils.getRidOfDoubleChar(
				"//", ((Package) VOHelper.getVO("PACKAGE", "appCmd", appCmd))
						.getPath() + "/reserve/");

		if (packageVO == null)
			throw new Exception("패키지 정보가 없습니다.");
		if (fileVO == null)
			throw new Exception("파일 정보가 없습니다.");
		if (UPLOAD_FULLPATH.equals("") || UPLOAD_FULLPATH == null)
			throw new Exception("패키지 폴더정보가 누락되었습니다.");

		File uploadFolder = new File(UPLOAD_FULLPATH);

		if (!uploadFolder.exists()) { // 예약파일폴더가 없을경우 신규생성
			uploadFolder.mkdir();
		}

		MultipartRequest multiPartRequest = 
				FileUpload.upload(request, UPLOAD_FULLPATH);
		
		Enumeration fileEnum = multiPartRequest.getFileNames();

		while (fileEnum.hasMoreElements()) {
			String tmpFilename = (String) fileEnum.nextElement();

			File tmpFile = multiPartRequest.getFile(tmpFilename);
			
			// 신규파일이름
			String NEWREVISION = Integer.valueOf(fileVO.getRevision()) + 1 + "";
			String NEWFILENAME = "v" + NEWREVISION + "_"
					+ fileVO.getLocalFileName();
			File newFile = new File(UPLOAD_FULLPATH + NEWFILENAME);

			// 파일이름 변경
			tmpFile.renameTo(newFile);

			// 예약파일 번호 증가
			VOHelper.modVO("FILE", "seq", fileSeq, "revision", NEWREVISION);

			// 디비등록
			VOHelper.addVO("RESERVE", new Reserve(fileSeq, tmpFile.getName(),
					NEWFILENAME, NEWREVISION, packageVO.getSeq()));
		}
	}
	
	/** 예약버젼증가 */
	public void increaseVersion(String seq) throws Exception {
		VOHelper.modVO("RESERVE", "seq", seq, "version", DateFormatUtils
				.format(System.currentTimeMillis(), "yyyyMMddHHmmss"));
	}

	/** 예약 삭제 */
	public void delReserve(String seq) throws Exception {
		VOHelper.delVO("RESERVE", "seq", seq);
	}
	
	/** 파일시퀀스에 해당하는 예약삭제 */
	public void delRservesByFileSeq(String fileSeq) throws Exception {
		VOHelper.delVOs("RESERVE", "fileSeq", fileSeq);
	}

	/** 패키지에 해당하는 예약 삭제 */
	public void deleteByPackageSeq(String packageSeq) throws Exception {
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "seq",
				packageSeq);
		List reserveList = (List) VOHelper.getVOList("RESERVE", "packageSeq",
				packageSeq);
		String PATH = BytoUtils.getRidOfDoubleChar("//", packageVO.getPath()
				+ "/reserve/");

		Iterator iterator = reserveList.iterator();

		while (iterator.hasNext()) {
			Reserve tmpReserve = (Reserve) iterator.next();
			File file = new File(PATH + tmpReserve.getNewFilename());

			if (file.exists())
				file.delete();
		}

		VOHelper.delVOs("RESERVE", "packageSeq", packageSeq);
	}

	/** 예약 물리파일 삭제 */
	public static void delPhysicReserve(String seq) throws Exception {
		Reserve reserveVO = (Reserve) VOHelper.getVO("RESERVE", "seq", seq);
		com.byto.vo.File fileVO = (com.byto.vo.File) VOHelper.getVO("FILE",
				"seq", reserveVO.getFileSeq());
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "seq",
				fileVO.getPackageSeq());

		String PATH = BytoUtils.getRidOfDoubleChar("//", packageVO.getPath()
				+ "/reserve/");
		String FILENAME = reserveVO.getNewFilename();
		File file = new File(PATH + FILENAME);

		if (file.exists())
			file.delete();
		else
			throw new Exception("파일이 존재하지 않습니다.");

	}

	/** 예약 상태 수정 */
	public void modState(String seq) throws Exception {
		Reserve reserveVO = (Reserve) VOHelper.getVO("RESERVE", "seq", seq);

		String newState = (reserveVO.getState().equals("1")) ? "0" : "1";

		VOHelper.modVO("RESERVE", "seq", seq, "state", newState);

	}

	/** 예약수정 */
	public void modReserve(String seq, String version, String applyTime,
			String use, String memo) throws Exception {
		Map searchMap = new HashMap();
		searchMap.put("seq", seq);

		Map replaceMap = new HashMap();
		replaceMap.put("version", version);
		replaceMap.put("applyTime", applyTime);
		replaceMap.put("memo", memo);

		VOHelper.modVO("RESERVE", searchMap, replaceMap);
	}

	/** 파일 강제 치환 */
	public void replaceReserve(String seq) throws Exception {
		Reserve reserveVO = (Reserve) VOHelper.getVO("RESERVE", "seq", seq);
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "seq",
				reserveVO.getPackageSeq());
		com.byto.vo.File fileVO = (com.byto.vo.File) VOHelper.getVO("FILE",
				"seq", reserveVO.getFileSeq());

		if (packageVO.getState().equals("1") && fileVO.getState().equals("1")) {
			throw new Exception("패키지 또는 파일의 상태가 중지일때만 사용 가능합니다.");
		}

		File orgFile = new File(packageVO.getPath() + fileVO.getLocalFileName());
		File newFile = new File(packageVO.getPath() + "/reserve/"
				+ reserveVO.getNewFilename());

		if (orgFile.exists() && newFile.exists()) {
			copy(newFile, orgFile);

			ReserveManageService.delPhysicReserve(reserveVO.getSeq());
			delReserve(reserveVO.getSeq());
		} else {
			throw new Exception("원본 또는 카피 파일이 존재하지 않습니다.");
		}
	}

	/** src에서 dst로 파일 이동 */
	void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/** 파일시퀀스로 물리예약파일들 삭제 */
	public void delPhysicReservesByFileSeq(String fileSeq)
			throws Exception {
		com.byto.vo.File fileVO = (com.byto.vo.File) VOHelper.getVO("FILE",
				"seq", fileSeq);
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "seq",
				fileVO.getPackageSeq());
		List reserveList = (List) VOHelper.getVOList("RESERVE", "fileSeq",
				fileSeq);

		String PATH = BytoUtils.getRidOfDoubleChar("//", packageVO.getPath()
				+ "/reserve/");

		Iterator i = reserveList.iterator();

		while (i.hasNext()) {
			Reserve tmpReserve = (Reserve) i.next();
			String FILENAME = tmpReserve.getNewFilename();
			File tmpFile = new File(PATH + FILENAME);

			if (tmpFile.exists())
				tmpFile.delete();
		}
	}

	/** reserve 갯수를 구함 */
	public JSONArray getReserveCount(String appCmd) throws Exception {
		Package packageVO = (Package) VOHelper.getVO("PACKAGE", "appCmd",
				appCmd);
		List fileVOList = (List) VOHelper.getVOList("FILE", "packageSeq",
				packageVO.getSeq());

		Map resultMap = new HashMap();
		List seqList = new ArrayList();

		Iterator i = fileVOList.iterator();

		while (i.hasNext()) {
			com.byto.vo.File tmpFileVO = (com.byto.vo.File) i.next();
			String fileSeq = tmpFileVO.getSeq();

			List reserveVOList = (List) VOHelper.getVOList("RESERVE",
					"fileSeq", tmpFileVO.getSeq());
			
			if(reserveVOList == null) continue;

			seqList.add(fileSeq);
			resultMap.put(fileSeq, reserveVOList.size());
		}

		resultMap.put("seqlist", seqList);
		return JSONArray.fromObject(resultMap);
	}

	public void deleteByFileSeq(String fileSeq) throws Exception {
		delPhysicReservesByFileSeq(fileSeq);
		delRservesByFileSeq(fileSeq);
	}
}