package com.byto.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.byto.service.LoginService;

@Controller
public class LoginController {
	
	@Autowired
	private LoginService sessionService;
	
	@RequestMapping("/ajax/login")
	public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		String passwd = request.getParameter("passwd");
		sessionService.processLogin(request.getSession(), id, passwd);
	}
	
	@RequestMapping("/admin/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		sessionService.processLogout(request.getSession());
		ServletContext sc = request.getSession().getServletContext();
		String contextPath = sc.getContextPath();
		response.sendRedirect(contextPath + "/login/login_page.byto");
	}
	
	@RequestMapping("/login/login_page")
	public ModelAndView loginPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		 return new ModelAndView("/login/login");
	}
}