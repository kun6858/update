package com.byto.vo;

import java.lang.reflect.Field;

public class Package implements ValueObject {

	private String seq, appName, appCmd, version, path, state, memo, use, remotePrefix;

	public Package(String seq, String appName, String appCmd, String version, String path, String state, String memo, String use, String remotePrefix) {
		this.seq = seq;
		this.appName = appName;
		this.appCmd = appCmd;
		this.version = version;
		this.path = path;
		this.state = state;
		this.memo = memo;
		this.use = use;
		this.remotePrefix = remotePrefix;
	}
	
	public String getRemotePrefix() {
		return remotePrefix;
	}

	public void setRemotePrefix(String remotePrefix) {
		this.remotePrefix = remotePrefix;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppCmd() {
		return appCmd;
	}

	public void setAppCmd(String appCmd) {
		this.appCmd = appCmd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	@Override
	public String toString() {
		return "Package [\n" +
				"\t" + "seq=" + seq + ",\n" +
				"\t" + "appName=" + appName + ",\n" +
				"\t" + "appCmd=" + appCmd + ",\n" +
				"\t" + "Version=" + version + ",\n" +
				"\t" + "Path=" + path + ",\n" +
				"\t" + "State=" + state + ",\n" +
				"\t" + "Memo=" + memo + ",\n" +
				"\t" + "Use=" + use + "\n]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof com.byto.vo.Package) {
			com.byto.vo.Package convertedObj = (com.byto.vo.Package) obj;

			if
			(
				seq.equals(convertedObj.getSeq()) &&
				appName.equals(convertedObj.getAppName()) &&
				appCmd.equals(convertedObj.getAppCmd()) &&
				version.equals(convertedObj.getVersion()) &&
				path.equals(convertedObj.getPath()) &&
				state.equals(convertedObj.getState()) &&
				memo.equals(convertedObj.getMemo()) &&
				use.equals(convertedObj.getUse()) &&
				remotePrefix.equals(convertedObj.getRemotePrefix())
			) return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		Field[] fields = com.byto.vo.Package.class.getFields();
		
		StringBuilder sb = new StringBuilder();
		
		for(int i=0; i<fields.length; i++) {
			sb.append(fields[i].getName());
		}
		
		return sb.toString().hashCode();
	}

}