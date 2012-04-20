package com.byto.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.byto.service.ReserveManageService;
import com.byto.util.BytoUtils;

@Controller
public class ReserveManageController {
	
	@Autowired private ReserveManageService reserveService;
	
	@RequestMapping("/ajax/get_reserve_info")
	public void getReserveInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		String fileSeq = request.getParameter("file_seq");
		
		JSONArray JSON = reserveService.getReserveList(appCmd, fileSeq);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(JSON.toString()); 
	}
	
	/*
	public void addReserve(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
	}
	*/
	
	@RequestMapping("/ajax/upload_reserve_file")
	public void uploadReserveFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		String fileSeq = request.getParameter("file_seq");
		
		reserveService.addReserve(appCmd, fileSeq);
	}
	
	@RequestMapping("/ajax/mod_reserve_version")
	public void modReserveVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		reserveService.increaseVersion(seq);
	}
	
	@RequestMapping("/ajax/del_reserve")
	public void delReserve(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		
		reserveService.delReserve(seq);
	}
	
	@RequestMapping("/ajax/del_physic_reserve")
	public void delPhysicReserve(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		
		reserveService.delPhysicReserve(seq);
	}
	
	@RequestMapping("/ajax/mod_state")
	public void modState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		
		reserveService.modState(seq);
	}
	
	@RequestMapping("/ajax/mod_reserve")
	public void modReserve(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		String version = request.getParameter("version");
		String applyTime = request.getParameter("apply_time");
		String use = request.getParameter("use");
		String memo = request.getParameter("memo");
		
		reserveService.modReserve(seq, version, applyTime, use, memo);
	}
	
	
	@RequestMapping("/ajax/replace_reserve")
	public void replaceReserve(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		
		reserveService.replaceReserve(seq);
	}
	
	@RequestMapping("/ajax/fill_reserve_count")
	public void fillReserveCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String appCmd = request.getParameter("app_cmd");
		
		JSONArray JSON = reserveService.getReserveCount(appCmd);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(JSON.toString()); 
	}
	
}