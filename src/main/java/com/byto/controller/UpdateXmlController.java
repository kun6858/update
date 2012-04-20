package com.byto.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.byto.service.UpdateXmlService;
import com.byto.util.StopWatch;
import com.byto.util.Time;
import com.byto.util.XMLogging;

@Controller
public class UpdateXmlController {
	
	@Autowired
	private UpdateXmlService updateService;
	
	@RequestMapping("/update")
	public ModelAndView getUpdateData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cmd = request.getParameter("cmd");
		Map resultMap = null;
		PrintWriter writer = response.getWriter();
		try {
			resultMap = updateService.getUpdateData(cmd, request);
			XMLogging.flush();
		} catch(Exception exception) {
			exception.printStackTrace();
			return new ModelAndView("/error/xml_error_page", "exception", exception);
		}
		return new ModelAndView("/client/update", "result", resultMap);
	}
	
	@RequestMapping("/ajax/reset_count")
	public void resetCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		String startHour = request.getParameter("start_hour");
		String endHour = request.getParameter("end_hour");
		
		updateService.resetCount(appCmd, startHour, endHour);
	}
}