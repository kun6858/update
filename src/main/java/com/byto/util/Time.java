package com.byto.util;

public class Time {
	private long time;
	
	public Time(long time) {
		this.time = time;
	}
	
	public long getMilliTime() {
		return time / 1000000L;
	}
	
	public long getNanoTime() {
		return time;
	}
	
	public double getSecondTime() {
		return (double) time / (double) 1000000000;
	}
}