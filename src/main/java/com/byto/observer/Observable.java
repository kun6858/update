package com.byto.observer;

public interface Observable {
	
	/**
	 * ������ ��� 
	 * @param o ��ϵ� ������
	 */
	public void registerObserver(Object o);
	
	/**
	 * ���������� ������ �˸�
	 * @param seq ������ ������
	 * @throws Exception
	 */
	public void notifyObservers(String seq) throws Exception;
}