package com.byto.vo;

public class History implements ValueObject {
	private String seq;
	
	private String packName;
	private String packCmd;
	private String packMemo;
	
	private String hourUsable;
	private String currHour;
	private String startHour;
	private String endHour;
	
	private String limitCount;
	private String currCount; 
	
	private String requestIP;
	private String responseIP;
	private String responsePort;
	
	private String currTime;

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getPackName() {
		return packName;
	}

	public void setPackName(String packName) {
		this.packName = packName;
	}

	public String getPackCmd() {
		return packCmd;
	}

	public void setPackCmd(String packCmd) {
		this.packCmd = packCmd;
	}

	public String getPackMemo() {
		return packMemo;
	}

	public void setPackMemo(String packMemo) {
		this.packMemo = packMemo;
	}

	public String getHourUsable() {
		return hourUsable;
	}

	public void setHourUsable(String hourUsable) {
		this.hourUsable = hourUsable;
	}

	public String getCurrHour() {
		return currHour;
	}

	public void setCurrHour(String currHour) {
		this.currHour = currHour;
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

	public String getCurrCount() {
		return currCount;
	}

	public void setCurrCount(String currCount) {
		this.currCount = currCount;
	}

	public String getRequestIP() {
		return requestIP;
	}

	public void setRequestIP(String requestIP) {
		this.requestIP = requestIP;
	}

	public String getResponseIP() {
		return responseIP;
	}

	public void setResponseIP(String responseIP) {
		this.responseIP = responseIP;
	}

	public String getResponsePort() {
		return responsePort;
	}

	public void setResponsePort(String responsePort) {
		this.responsePort = responsePort;
	}

	public String getCurrTime() {
		return currTime;
	}

	public void setCurrTime(String currTime) {
		this.currTime = currTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currCount == null) ? 0 : currCount.hashCode());
		result = prime * result
				+ ((currHour == null) ? 0 : currHour.hashCode());
		result = prime * result
				+ ((currTime == null) ? 0 : currTime.hashCode());
		result = prime * result + ((endHour == null) ? 0 : endHour.hashCode());
		result = prime * result
				+ ((hourUsable == null) ? 0 : hourUsable.hashCode());
		result = prime * result
				+ ((limitCount == null) ? 0 : limitCount.hashCode());
		result = prime * result + ((packCmd == null) ? 0 : packCmd.hashCode());
		result = prime * result
				+ ((packMemo == null) ? 0 : packMemo.hashCode());
		result = prime * result
				+ ((packName == null) ? 0 : packName.hashCode());
		result = prime * result
				+ ((requestIP == null) ? 0 : requestIP.hashCode());
		result = prime * result
				+ ((responseIP == null) ? 0 : responseIP.hashCode());
		result = prime * result
				+ ((responsePort == null) ? 0 : responsePort.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result
				+ ((startHour == null) ? 0 : startHour.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		History other = (History) obj;
		if (currCount == null) {
			if (other.currCount != null)
				return false;
		} else if (!currCount.equals(other.currCount))
			return false;
		if (currHour == null) {
			if (other.currHour != null)
				return false;
		} else if (!currHour.equals(other.currHour))
			return false;
		if (currTime == null) {
			if (other.currTime != null)
				return false;
		} else if (!currTime.equals(other.currTime))
			return false;
		if (endHour == null) {
			if (other.endHour != null)
				return false;
		} else if (!endHour.equals(other.endHour))
			return false;
		if (hourUsable == null) {
			if (other.hourUsable != null)
				return false;
		} else if (!hourUsable.equals(other.hourUsable))
			return false;
		if (limitCount == null) {
			if (other.limitCount != null)
				return false;
		} else if (!limitCount.equals(other.limitCount))
			return false;
		if (packCmd == null) {
			if (other.packCmd != null)
				return false;
		} else if (!packCmd.equals(other.packCmd))
			return false;
		if (packMemo == null) {
			if (other.packMemo != null)
				return false;
		} else if (!packMemo.equals(other.packMemo))
			return false;
		if (packName == null) {
			if (other.packName != null)
				return false;
		} else if (!packName.equals(other.packName))
			return false;
		if (requestIP == null) {
			if (other.requestIP != null)
				return false;
		} else if (!requestIP.equals(other.requestIP))
			return false;
		if (responseIP == null) {
			if (other.responseIP != null)
				return false;
		} else if (!responseIP.equals(other.responseIP))
			return false;
		if (responsePort == null) {
			if (other.responsePort != null)
				return false;
		} else if (!responsePort.equals(other.responsePort))
			return false;
		if (seq == null) {
			if (other.seq != null)
				return false;
		} else if (!seq.equals(other.seq))
			return false;
		if (startHour == null) {
			if (other.startHour != null)
				return false;
		} else if (!startHour.equals(other.startHour))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "History [seq=" + seq + ", packName=" + packName + ", packCmd="
				+ packCmd + ", packMemo=" + packMemo + ", hourUsable="
				+ hourUsable + ", currHour=" + currHour + ", startHour="
				+ startHour + ", endHour=" + endHour + ", limitCount="
				+ limitCount + ", currCount=" + currCount + ", requestIP="
				+ requestIP + ", responseIP=" + responseIP + ", responsePort="
				+ responsePort + ", currTime=" + currTime + "]";
	}
}