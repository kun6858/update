package com.byto.observer;

public interface Observable {
	
	/**
	 * 옵저버 등록 
	 * @param o 등록될 옵저버
	 */
	public void registerObserver(Object o);
	
	/**
	 * 옵저버에게 삭제를 알림
	 * @param seq 삭제될 시퀀스
	 * @throws Exception
	 */
	public void notifyObservers(String seq) throws Exception;
}