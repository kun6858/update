package com.byto.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.byto.service.DownloadManageService;

@Controller
public class DownloadManageController {
	
	@Autowired private DownloadManageService downService;
	
	@RequestMapping("/ajax/add_download_hour")
	public void addDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		String startHour = request.getParameter("start_hour");
		String endHour = request.getParameter("end_hour");
		String limitCount = request.getParameter("limit_count");

		downService.addDownload(appCmd, startHour, endHour, limitCount); 
	}

	@RequestMapping("/ajax/get_each_download_info")
	public void getDownloads(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List hourList = downService.getDownloads(request.getParameter("app_cmd"));
		JSONArray json = JSONArray.fromObject(hourList) ;
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(json.toString()); 
	}
	
	@RequestMapping("/ajax/del_download_hour")
	public void delDownload(HttpServletRequest request, HttpServletResponse response) throws Exception {
		downService.delDownload(request.getParameter("seq"));
	}
}