package com.byto.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.byto.service.PackageServerManageService;

@Controller
public class PackageServerController {
	
	@Autowired
	private PackageServerManageService packageServerService;

	@RequestMapping("/ajax/get_each_package_server")
	public void getEachPackageServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		
		Map returnMap = packageServerService.getPackageServers(appCmd);
		
		JSONArray json = JSONArray.fromObject(returnMap) ;
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(json.toString()); 
	}

	@RequestMapping("/ajax/del_package_server")
	public void delPackageServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("appCmd");
		String host = request.getParameter("host");
		
		packageServerService.delPackageServer(appCmd, host);
	}
	
	@RequestMapping("/ajax/add_package_server")
	public void addPackageServr(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("appCmd");
		String host = request.getParameter("host");
		
		packageServerService.addPackageServer(appCmd, host);
	}
}