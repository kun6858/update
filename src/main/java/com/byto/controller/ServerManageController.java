package com.byto.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.byto.service.ServerManageService;

@Controller
public class ServerManageController {
	
	@Autowired
	private ServerManageService serverService;
	
	/**
	 * 서버 정보 추가
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@RequestMapping("/ajax/add_server")
	public void addServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String host = request.getParameter("host");
		String port = request.getParameter("port");
		String state = request.getParameter("state");
		
		serverService.addServer(host, port, state);
	}
	
	/**
	 * 서버 정보 변경
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/mod_server")
	public void modServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		String host = request.getParameter("host");
		String port = request.getParameter("port"); 
		
		serverService.modServer(seq, host, port);
	}
	
	/**
	 * 서버 상태 변경
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/mod_server_state")
	public void modServerState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		String state = request.getParameter("state");
		serverService.modServerState(seq, state);
	}

	/**
	 * 서버 목록 표시(non-ajax)
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 */
	@RequestMapping("/admin/server_manage")
	public ModelAndView listServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/admin/server_manage");
		modelAndView.addObject("serverList", serverService.getServerList());

		return modelAndView;
	}
	
	/**
	 * 서버 목록 표시(ajax)
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 */
	@RequestMapping("/ajax/list_server")
	public void listServerInJson(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONArray json = JSONArray.fromObject(serverService.getServerList());
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(json.toString()); 
	}

	/**
	 * 서버 정보 삭제
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/del_server")
	public void delServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String seq = request.getParameter("seq");
		
		serverService.delServer(seq);
	}
}