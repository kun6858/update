package com.byto.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.byto.service.PackageManageService;
import com.byto.util.BytoUtils;

@Controller
public class PackageManageController {
	
	@Autowired
	private PackageManageService packService;
	
	/** ��Ű������ ������ �̵� */
	@RequestMapping("/admin/package_manage")
	public ModelAndView packageManage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return new ModelAndView("/admin/package_manage");
	}
	
	/** ��Ű����� ǥ�� */
	@RequestMapping("/ajax/list_package")
	public void listPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONArray json = JSONArray.fromObject(packService.getPackageList());
		
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");  
		response.getWriter().write(json.toString()); 
	}

	/** ��Ű�� �߰� */
	@RequestMapping("/ajax/add_package")
	public void addPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println(BytoUtils.paramToString(request));
		
		packService.addPackage(
			request.getParameter("app_name"),
			request.getParameter("app_cmd"),
			request.getParameter("path"),
			request.getParameter("state"),
			request.getParameter("memo"),
			request.getParameter("use"),
			request.getParameter("remote_prefix")
		);
	}
		
	/** ��Ű�� ���� */
	@RequestMapping("/ajax/del_package")
	public void delPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		packService.delPackage(request.getParameter("seq"));
	}

	/**
	 * ��Ű�� ���¸� �����Ѵ�.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/mod_package_state")
	public void modPackageState(HttpServletRequest request, HttpServletResponse response) throws Exception {
		packService.modPackageState(request.getParameter("seq"));
	}
	
	/**
	 * ��Ű�� �ð� ���� ���θ� �����Ѵ�.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/mod_package_use")
	public void modPackageUse(HttpServletRequest request, HttpServletResponse response) throws Exception {
		packService.modPackageUse(request.getParameter("seq"));
	}
	
	/**
	 * ��Ű�� ������ �����Ѵ�.
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws PathNotExistException
	 * @throws DuplicateAppCmdException
	 */
	@RequestMapping("/ajax/mod_package")
	public void modPackage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		packService.modPackage(
			request.getParameter("seq"),
			request.getParameter("app_name"),
			request.getParameter("app_cmd"), 
			request.getParameter("path"),
			request.getParameter("memo"),
			request.getParameter("remote_prefix"),
			request.getParameter("version")
		);
	}
	
	/**
	 * ��Ű�� ������ �������Ѵ�.
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/ajax/mod_package_version")
	public void modPackageVersion(HttpServletRequest request, HttpServletResponse response) throws Exception {
		packService.modPackageVersion(request.getParameter("seq"));
	}
}
