package com.byto.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.byto.service.FileManageService;
import com.byto.service.PackageManageService;

@Controller
public class FileManageController {
	
	@Autowired private FileManageService fileService;
	@Autowired private PackageManageService packService;
	@Autowired private ServletContext servletContext;
	
	@RequestMapping("/admin/file_manage")
	public ModelAndView listFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map resultMap = new HashMap();

		resultMap.put("packageInfo", packService.getPackageInfomationInJson());

		return new ModelAndView("/admin/file_manage", "resultMap", resultMap);
	}
	
	@RequestMapping("/ajax/list_file")
	public void getJsonList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		
		JSONArray json = fileService.getFileList(appCmd);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(json.toString()); 
	}
	
	@RequestMapping("/ajax/del_file")
	public void delFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		fileService.delFile(request.getParameter("seq"));
	}
	
	@RequestMapping("/ajax/mod_file_state")
	public void modPackageState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		fileService.modFileState(request.getParameter("seq"));
	}

	@RequestMapping("/ajax/mod_file")
	public void modFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		fileService.modFile(
				request.getParameter("seq"),
				request.getParameter("remote_file_name"),
				request.getParameter("reg"),
				request.getParameter("tag_name"),
				request.getParameter("type"),
				request.getParameter("version"));
	}
	
	@RequestMapping("/ajax/mod_file_version")
	public void modFileVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		fileService.modFileVersion(
			request.getParameter("seq"));
	}
	
	@RequestMapping("/ajax/add_file")
	public void addFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		fileService.addFile(
			request.getParameter("selected_app_cmd"), 
			request.getParameter("remote_file_name"), 
			request.getParameter("local_file_name"),
			request.getParameter("reg"), 
			request.getParameter("tag_name"),
			request.getParameter("type")
		);
	}
	
	@RequestMapping("/ajax/file_upload")
	public void uploadFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		fileService.uploadFile(appCmd);
	}
	
	@RequestMapping("/ajax/del_physic_file")
	public void delPhysicFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String localFileName = request.getParameter("local_file_name");
		String appCmd = request.getParameter("app_cmd");
		
		fileService.delPhysicFile(appCmd,localFileName);
	}

}