package com.gi.server.rest.html;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLDecoder;

import javax.servlet.ServletContext;

import com.gi.engine.util.VersionUtil;
import com.gi.engine.util.common.PathUtil;

public class AdministratorHTML {

	private static String htmlTemplate = null;

	private static String getHtmlTemplate() {
		if (htmlTemplate == null) {
			try {
				String htmlDir = URLDecoder.decode(AdministratorHTML.class
						.getResource("").getFile(), "utf-8");
				BufferedReader br = new BufferedReader(new FileReader(htmlDir
						+ "administrator.html"));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				htmlTemplate = sb.toString();
				htmlTemplate = htmlTemplate.replaceAll("\\$\\{VERSION\\}",
						VersionUtil.getCurrentversion());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return htmlTemplate;
	}

	private String html = null;

	private String getHtml() {
		if (html == null) {
			html = AdministratorHTML.getHtmlTemplate();
		}

		return html;
	}

	public void setCatalog(String catalog) {
		html = getHtml().replaceAll("\\$\\{CATALOG\\}", catalog);
	}

	public void setHome(String home) {
		html = getHtml().replaceAll("\\$\\{HOME\\}", home);
	}

	public void setContextRoot(String contextRoot) {
		html = getHtml().replaceAll("\\$\\{CONTEXT_ROOT\\}", contextRoot);
	}

	public void setHeader(String header) {
		html = getHtml().replaceAll("\\$\\{HEADER\\}", header);
	}

	public void setRestBody(String restBody) {
		html = getHtml().replaceAll("\\$\\{RESTBODY\\}", restBody);
	}

	public void setTitle(String title) {
		html = getHtml().replaceAll("\\$\\{TITLE\\}", title);
	}

	public String toString() {
		if (html == null) {
			return "";
		} else {
			return getHtml();
		}
	}

	static public String getLoginForm() {
		StringBuilder sb = new StringBuilder();
		sb
				.append("<form action='${POST_URI}' method='post'><table id='authLoginTable'><tbody>");
		sb.append("<tr><td colspan='2'><b>Login</b></td></tr>");
		sb
				.append("<tr><td>User Name</td><td><input type='text' name='username' /></td></tr>");
		sb
				.append("<tr><td>Password</td><td><input type='password' name='password' /></td></tr>");
		sb
				.append("<tr><td colspan='2'><input type='submit' onclick='this.form.method = 'post';'  value='Login' /></td></tr>");
		sb.append("</tbody></table></form>");

		return sb.toString();
	}

	static public String getServerInformation(ServletContext context) {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>Server Information:</b>&nbsp;&nbsp;"
				+ context.getServerInfo() + "<br/>");
		sb.append("<b>Java Version:</b>&nbsp;&nbsp;"
				+ System.getProperty("java.runtime.name") + " "
				+ System.getProperty("java.runtime.version") + "<br/>");
		sb.append("<b>Deploy Path:</b>&nbsp;&nbsp;"
				+ PathUtil.realPathToFake(context.getRealPath(""))
				+ "<br/><br/>");
		Runtime r = Runtime.getRuntime();
		sb.append("<b>Java Runtime Information:</b><br/>");
		sb.append("&nbsp;&nbsp;Available Processors:&nbsp;&nbsp;"
				+ r.availableProcessors() + "<br/>");
		sb.append("&nbsp;&nbsp;Total Memory:&nbsp;&nbsp;" + r.totalMemory()
				/ 1048576 + " MB<br/>");
		sb.append("&nbsp;&nbsp;Free Memory:&nbsp;&nbsp;" + r.freeMemory()
				/ 1048576 + " MB<br/>");
		sb.append("&nbsp;&nbsp;Max Memory:&nbsp;&nbsp;" + r.maxMemory()
				/ 1048576 + " MB<br/><br/>");

		return sb.toString();
	}
}
