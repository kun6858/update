package com.byto.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.byto.vo.Count;
import com.byto.vo.Package;

@Component
public class XMLCache {

	private static XMLCache instance = new XMLCache();
	private Map cache = null;
	private List counts = null;
	
	private XMLCache() {
		this.cache = new HashMap();
		this.counts = new ArrayList();
	}
	
	public static XMLCache getInstance() { 
		return instance; 
	}
	
	public void addCount(Count count) { counts.add(count); }
	
	public void clear() {
		cache.clear();
	}

	public void setCache(String appCmd, Map resultMap) {
		cache.put(appCmd, resultMap);
	}

	public void delCache(String appCmd) {
		cache.remove(appCmd);
	}

	public Map getCache(String appCmd) {
		Map retMap = (Map) cache.get(appCmd);
		
		if(retMap == null)
			retMap = new HashMap();
		
		return retMap;
	}
	
	public List getCounts() {
		return counts;
	}

	public void resetCounts() {
		counts.clear();
	}

	public void delCount(Count count) {
		counts.remove(count);
	}

	public Count getCount(String packageSeq, String startHour, String endHour) {
		String date = DateFormatUtils.format(System.currentTimeMillis(), "yyyyMMdd");
		List countList = (List) getCounts();
		Count targetCount = null;
		
		startHour = String.valueOf(Integer.valueOf(startHour));
		endHour = String.valueOf(Integer.valueOf(endHour));

		try {
			Iterator i = countList.iterator();
			
			while (i.hasNext()) {
				Count count = (Count) i.next();
				if (count.getPackageSeq().equals(packageSeq)
						&& count.getDate().equals(date)
						&& count.getStartHour().equals(startHour)
						&& count.getEndHour().equals(endHour)) {
					targetCount = count;
					break;
				}
			}
		} catch (NullPointerException ex) {
			return null;
		}

		return targetCount;
	}
}