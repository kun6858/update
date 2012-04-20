package com.byto.exception.resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class BaseExceptionResolver implements HandlerExceptionResolver {

	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception exception) {
		
		exception.printStackTrace();
		
		ModelAndView modelAndView = new ModelAndView("/error/error");
		
		modelAndView.addObject("exception", exception);
		
		return modelAndView;
	}
}