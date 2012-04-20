package com.byto.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckFilter implements Filter {

	private FilterConfig config;

	public LoginCheckFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("Instance created of " + getClass().getName());
		this.config = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws java.io.IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		ServletContext context = config.getServletContext();

		String logged = (String) session.getAttribute("login");
		
		if (logged == null) {
			HttpServletResponse hResponse = ((HttpServletResponse) response);
			hResponse.sendRedirect(context.getContextPath() + "/login/login_page.byto");
		}

		chain.doFilter(request, response);
	}

	public void destroy() {
	}
}