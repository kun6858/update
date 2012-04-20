package com.byto.vo;

import org.apache.commons.lang.time.DateFormatUtils;

public class Count {

	private String packageSeq, downSeq, date, startHour, endHour;
	private int downCount;
	
	public Count(String packageSeq, String downSeq, String startHour,
			String endHour, int downCount) {
		this.packageSeq = packageSeq;
		this.downSeq = downSeq;
		this.date = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
		this.startHour = startHour;
		this.endHour = endHour;
		this.downCount = downCount;
	}

	public String getPackageSeq() {
		return packageSeq;
	}

	public void setPackageSeq(String packageSeq) {
		this.packageSeq = packageSeq;
	}

	public String getDownSeq() {
		return downSeq;
	}

	public void setDownSeq(String downSeq) {
		this.downSeq = downSeq;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public int getDownCount() {
		return downCount;
	}

	public void setDownCount(int downCount) {
		this.downCount = downCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + downCount;
		result = prime * result + ((downSeq == null) ? 0 : downSeq.hashCode());
		result = prime * result + ((endHour == null) ? 0 : endHour.hashCode());
		result = prime * result
				+ ((packageSeq == null) ? 0 : packageSeq.hashCode());
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
		Count other = (Count) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (downCount != other.downCount)
			return false;
		if (downSeq == null) {
			if (other.downSeq != null)
				return false;
		} else if (!downSeq.equals(other.downSeq))
			return false;
		if (endHour == null) {
			if (other.endHour != null)
				return false;
		} else if (!endHour.equals(other.endHour))
			return false;
		if (packageSeq == null) {
			if (other.packageSeq != null)
				return false;
		} else if (!packageSeq.equals(other.packageSeq))
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
		return "Count [packageSeq=" + packageSeq + ", downSeq=" + downSeq
				+ ", date=" + date + ", startHour=" + startHour + ", endHour="
				+ endHour + ", downCount=" + downCount + "]";
	}
	
}