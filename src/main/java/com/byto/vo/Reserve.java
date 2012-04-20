package com.byto.vo;

import java.io.IOException;

import org.apache.commons.lang.time.DateFormatUtils;

import com.byto.util.Sequence;

public class Reserve implements ValueObject {

	private String seq, fileSeq, orgFilename, newFilename, version, state,
			applyTime, size, revision, memo, packageSeq;
	
	//신규파일 등록시
	public Reserve(String fileSeq, String orgFilename, String newFilename, String revision, String packageSeq) throws IOException {
		this.seq = Sequence.seq();
		this.fileSeq = fileSeq;
		this.orgFilename = orgFilename;
		this.newFilename = newFilename;
		this.version = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmss");
		this.state = "1";
		this.applyTime = "";
		this.revision = revision;
		this.memo = "";
		this.packageSeq = packageSeq;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getFileSeq() {
		return fileSeq;
	}

	public void setFileSeq(String fileSeq) {
		this.fileSeq = fileSeq;
	}

	public String getOrgFilename() {
		return orgFilename;
	}

	public void setOrgFilename(String orgFilename) {
		this.orgFilename = orgFilename;
	}

	public String getNewFilename() {
		return newFilename;
	}

	public void setNewFilename(String newFilename) {
		this.newFilename = newFilename;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getApplyTime() {
		return applyTime;
	}

	public void setApplyTime(String applyTime) {
		this.applyTime = applyTime;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getPackageSeq() {
		return packageSeq;
	}

	public void setPackageSeq(String packageSeq) {
		this.packageSeq = packageSeq;
	}

	@Override
	public String toString() {
		return "Reserve [seq=" + seq + ", fileSeq=" + fileSeq
				+ ", orgFilename=" + orgFilename + ", newFilename="
				+ newFilename + ", version=" + version + ", state=" + state
				+ ", applyTime=" + applyTime + ", size=" + size + ", use="
				+ ", revision=" + revision + ", memo=" + memo
				+ ", packageSeq=" + packageSeq + "]";
	}

}
