package com.gi.server.rest;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wuyf Redirect from /rest to /rest/service/
 * 
 */
public class RestServlet extends HttpServlet {

	private static final long serialVersionUID = 6990893363692166944L;

	public RestServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		response.sendRedirect(uri + "service/");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (!uri.endsWith("/")) {
			uri += "/";
		}
		response.sendRedirect(uri + "service/");
	}

}
