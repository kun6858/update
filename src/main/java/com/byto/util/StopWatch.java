package com.byto.util;

public class StopWatch {
	
	private long startTime;
	private long endTime;
	
	private static StopWatch instance = new StopWatch();
	
	private StopWatch(){}
	
	public static StopWatch getInstance() {
		return instance;
	}
	
	public void start() {
		this.startTime = System.nanoTime();
	}
	
	public void stop() {
		this.endTime = System.nanoTime();
	}
	
	public Time getElapsedTime() {
		return new Time(endTime - startTime);
	}

}