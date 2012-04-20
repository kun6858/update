package com.byto.vo;

public class PackageServer implements ValueObject {

	private String seq, packageSeq, serverSeq;
	
	public PackageServer(String seq, String packageSeq, String serverSeq) {
		super();
		this.seq = seq;
		this.packageSeq = packageSeq;
		this.serverSeq = serverSeq;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getPackageSeq() {
		return packageSeq;
	}

	public void setPackageSeq(String packageSeq) {
		this.packageSeq = packageSeq;
	}

	public String getServerSeq() {
		return serverSeq;
	}

	public void setServerSeq(String serverSeq) {
		this.serverSeq = serverSeq;
	}

	@Override
	public String toString() {
		return "PackageServer [seq=" + seq + ", packageSeq=" + packageSeq
				+ ", serverSeq=" + serverSeq + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof com.byto.vo.PackageServer) {
			com.byto.vo.PackageServer convertedObj = (com.byto.vo.PackageServer) obj;

			if
			(
				seq.equals(convertedObj.getSeq()) &&
				packageSeq.equals(convertedObj.getPackageSeq()) &&
				serverSeq.equals(convertedObj.getServerSeq())
			) return true;
		}
		return false;
	}


	
}