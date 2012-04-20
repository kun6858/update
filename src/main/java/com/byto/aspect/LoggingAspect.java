package com.byto.aspect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.byto.util.StopWatch;
import com.byto.util.Time;

public class LoggingAspect {
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	StopWatch stopWatch;
	
	public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			
			stopWatch.start();
			Object retValue = joinPoint.proceed();
			return retValue;
			
		} catch (Throwable e) {
			
			throw e;
			
		} finally {
			
			stopWatch.stop();
			
			Time time = stopWatch.getElapsedTime();
			log.info("XML积己俊 家夸等 矫埃 : " + time.getMilliTime() + " ms");
			
		}
	}
}
