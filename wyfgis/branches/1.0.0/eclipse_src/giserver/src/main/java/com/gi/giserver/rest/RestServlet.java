package com.gi.giserver.rest;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Wu Yongfeng
 * Redirect from /rest  to /rest/services/
 *
 */
public class RestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public RestServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		response.sendRedirect(uri + "/service/");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		response.sendRedirect(uri + "/service/");
	}

}
