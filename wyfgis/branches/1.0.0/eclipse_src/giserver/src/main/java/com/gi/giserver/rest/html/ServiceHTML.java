package com.gi.giserver.rest.html;

public class ServiceHTML {

	static public String getHeader(String contextRoot) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />");
		sb.append("<title>${TITLE}</title>");
		sb.append("<link href='" + contextRoot + "/css' rel='stylesheet' type='text/css'/>");
		sb.append("</head>");

		sb.append("<body>");
		sb.append("<table width='100%' id='userTable'>");
		sb.append("<tbody>");
		sb.append("<tr>");
		sb.append("<td id='titlecell'>REST Services Directory [Powered by GIServer]</td>");
		sb.append("</tr>");
		sb.append("</tbody>");
		sb.append("</table>");

		return sb.toString();
	}

	static public String getNav(String contextRoot) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%' id='navTable'><tbody><tr valign='top'><td id='breadcrumbs'>");
		sb.append("<a href='" + contextRoot + "/rest/service/'>Home</a>${CATALOG}");
		sb.append("</td><td align='right' id='help'>");
		// sb.append("<a href='?f=help' target='_blank'>API Reference</a>");
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

}
