package com.gi.giserver.rest.html;

import javax.servlet.ServletContext;

public class AdministratorHTML {

	static public String getHeader(String contextRoot) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>${TITLE}</title>");
		sb.append("<link href='" + contextRoot + "/css' rel='stylesheet' type='text/css'/>");
		sb.append("</head>");

		sb.append("<body>");
		sb.append("<table width='100%' id='userTable'>");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td id='titlecell'>GIServer Administrator Console</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		return sb.toString();
	}

	static public String getNav(String contextRoot) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%' id='authNavTable'><tbody><tr valign='top'><td id='breadcrumbs'>");
		sb.append("<a href='${HOME}'>Home</a>${CATALOG}");
		sb.append("</td></tr></tbody></table>");

		return sb.toString();
	}

	static public String getH2() {
		return "<h2>${TITLE}</h2>";
	}

	static public String getRestBody() {
		return "<div class='restBody'>${RESTBODY}</div>";
	}

	static public String getFooter() {
		return "</body></html>";
	}

	static public String getLoginForm() {
		StringBuilder sb = new StringBuilder();
		sb.append("<form action='${POST_URI}' method='post'><table id='authLoginTable'><tbody>");
		sb.append("<tr><td colspan='2'><b>Login</b></td></tr>");
		sb.append("<tr><td>User Name</td><td><input type='text' name='username' /></td></tr>");
		sb.append("<tr><td>Password</td><td><input type='password' name='password' /></td></tr>");
		sb.append("<tr><td colspan='2'><input type='submit' value='Login' /></td></tr>");
		sb.append("</tbody></table></form>");

		return sb.toString();
	}

	static public String getServerInformation(ServletContext context) {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>Server Information:</b>" + "&nbsp;&nbsp;" + context.getServerInfo() + "<br/>");
		sb.append("<b>GIServer Deploy Path:</b>" + "&nbsp;&nbsp;" + context.getRealPath("") + "<br/><br/>");
		Runtime r = Runtime.getRuntime();
		sb.append("<b>JVM Information:</b><br/>");
		sb.append("&nbsp;&nbsp;Available Processors:&nbsp;&nbsp;" + r.availableProcessors() + "<br/>");
		sb.append("&nbsp;&nbsp;Total Memory:&nbsp;&nbsp;" + r.totalMemory() / 1048576 + " MB<br/>");
		sb.append("&nbsp;&nbsp;Free Memory:&nbsp;&nbsp;" + r.freeMemory() / 1048576 + " MB<br/>");
		sb.append("&nbsp;&nbsp;Max Memory:&nbsp;&nbsp;" + r.maxMemory() / 1048576 + " MB<br/><br/>");

		return sb.toString();
	}
}
