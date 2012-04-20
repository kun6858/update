package com.byto.vo;

import com.byto.util.BytoUtils;

public class File implements ValueObject {

	private String seq, remoteFileName, localFileName, version,
			packageSeq, type, reg, state, size, tagName, remote, revision, finalFilename, downPath;

	public File(){}
	
	public File(String localFileName, String type, String state, String size) {
		this.state = state;
		this.localFileName = localFileName;
		this.type = type;
		this.size = size;
	}
	
	public File(String seq, String packageSeq,
			String remoteFileName, String localFileName, String version,
			String reg, String state, String tagName, String type) {
		this.seq = seq;
		this.packageSeq = packageSeq;
		this.remoteFileName = remoteFileName;
		this.localFileName = localFileName;
		this.version = version;
		this.reg = reg;
		this.state = state;
		this.tagName = tagName;
		this.type = type;
		this.revision = "0";
	}
	
	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getRemote() {
		return remote;
	}

	public void setRemote(String remote) {
		this.remote = remote;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getPackageSeq() {
		return packageSeq;
	}

	public void setPackageSeq(String packageSeq) {
		this.packageSeq = packageSeq;
	}

	public String getSeq() {
		return seq;
	}

	public String getRemoteFileName() {
		return remoteFileName;
	}

	public void setRemoteFileName(String remoteFileName) {
		this.remoteFileName = remoteFileName;
	}

	public String getLocalFileName() {
		return localFileName;
	}

	public void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReg() {
		return reg;
	}

	public void setReg(String reg) {
		this.reg = reg;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getFilename() { 
		int index = localFileName.lastIndexOf('.');
		
		if (index > 0 && index <= localFileName.length() - 2 ) {
			return localFileName.substring(0, index);
		} else {
			return localFileName;
		}
	}
	
	
	/*
	@Override
	public String toString() {
		return "\n" + localFileName;
	}
	*/

	@Override
	public String toString() {
		return "File [seq=" + seq + ", remoteFileName=" + remoteFileName
				+ ", localFileName=" + localFileName + ", version=" + version
				+ ", packageSeq=" + packageSeq + ", type=" + type + ", reg="
				+ reg + ", state=" + state + ", size=" + size + ", tagName="
				+ tagName + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof com.byto.vo.File) {
			com.byto.vo.File convertedObj = (com.byto.vo.File) obj;

			if
			(
				seq.equals(convertedObj.getSeq()) &&
				remoteFileName.equals(convertedObj.getRemoteFileName()) &&
				localFileName.equals(convertedObj.getLocalFileName()) &&
				version.equals(convertedObj.getVersion()) &&
				packageSeq.equals(convertedObj.getPackageSeq()) &&
				type.equals(convertedObj.getType()) &&
				reg.equals(convertedObj.getReg()) &&
				state.equals(convertedObj.getState()) &&
				//size.equals(convertedObj.getSize()) &&
				tagName.equals(convertedObj.getTagName())
			) return true;
		}
		return false;
	}

	public String getFinalFilename() {
		return finalFilename;
	}

	public void setFinalFilename(String finalFilename) {
		this.finalFilename = finalFilename;
	}

	public String getDownPath() {
		return BytoUtils.getRidOfDoubleChar("//", remote + finalFilename);
	}
}