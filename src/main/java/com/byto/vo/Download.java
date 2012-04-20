package com.byto.vo;


public class Download implements Comparable, ValueObject, Cloneable {

	String seq, packageSeq, startHour, endHour, limitCount, appCmd; //차후 appCmd삭제

	public Download(String seq, String packageSeq, String startHour,
			String endHour, String limitCount) {
		this.seq = seq;
		this.packageSeq = packageSeq;
		this.startHour = startHour;
		this.endHour = endHour;
		this.limitCount = limitCount;
	}

	public String getAppCmd() {
		return appCmd;
	}

	public void setAppCmd(String appCmd) {
		this.appCmd = appCmd;
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

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public String getLimitCount() {
		return limitCount;
	}

	public void setLimitCount(String limitCount) {
		this.limitCount = limitCount;
	}

	public int compareTo(Object newObj) {
		int oldStartHour = new Integer(this.startHour).intValue();
		int newStartHour = new Integer(((Download) newObj).startHour)
				.intValue();

		if (oldStartHour == newStartHour)
			return 0;
		else if (oldStartHour > newStartHour)
			return 1;
		else
			return -1;
	}

	@Override
	public String toString() {
		return "Download [\n" + "\t" + "seq=" + seq + ",\n" + "\t"
				+ "packageSeq=" + packageSeq + ",\n" + "\t" + "startHour="
				+ startHour + ",\n" + "\t" + "endHour=" + endHour + ",\n"
				+ "\t" + "limitCount=" + limitCount + ",\n" + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Download download = (Download) super.clone();
		return download;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof com.byto.vo.Download) {
			com.byto.vo.Download convertedObj = (com.byto.vo.Download) obj;

			if (seq.equals(convertedObj.getSeq())
					&& packageSeq.equals(convertedObj.getPackageSeq())
					&& startHour.equals(convertedObj.getStartHour())
					&& endHour.equals(convertedObj.getEndHour())
					&& limitCount.equals(convertedObj.getLimitCount()))
				return true;
		}
		return false;
	}
}
