package com.gi.server.rest.html;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URLDecoder;

import com.gi.engine.util.VersionUtil;

public class ServiceHTML {

	private static String htmlTemplate = null;

	private static String getHtmlTemplate() {
		if (htmlTemplate == null) {
			try {
				String htmlDir = URLDecoder.decode(ServiceHTML.class
						.getResource("").getFile(), "utf-8");
				BufferedReader br = new BufferedReader(new FileReader(htmlDir
						+ "service.html"));
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
			html = ServiceHTML.getHtmlTemplate();
		}

		return html;
	}

	public void setCatalog(String catalog) {
		html = getHtml().replaceAll("\\$\\{CATALOG\\}", catalog);
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

}
