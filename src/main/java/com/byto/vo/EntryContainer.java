package com.byto.vo;

import java.util.ArrayList;
import java.util.List;

public class EntryContainer {
	private List<Object> entries = new ArrayList<Object>();

	public List<Object> getEntries() {
		return entries;
	}

	public void setEntries(List<Object> entries) {
		this.entries = entries;
	}

	public void addEntries(Object entry) {
		entries.add(entry);
	}

	@Override
	public String toString() {
		return "EntryContainer [entries=" + entries + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}