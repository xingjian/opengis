package com.gi.giserver.rest.service.geometryservice;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONException;
import org.json.JSONObject;

import com.gi.giserver.core.config.ConfigManager;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/GeometryService")
public class Resource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f) {
		return result(f);
	}

	private String result(String f) {
		String result = null;

		try {
			boolean needToken = ConfigManager.getServerConfig().isInternalServiceNeedTokenVerify();

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(needToken);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(needToken);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(boolean needToken) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			obj.put("about", "Powered by GIServer");
			obj.put("needToken", needToken);

			result = obj.toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(boolean needToken) {
		String result = "";

		String contextRoot = context.getContextPath();

		String restBody = "";
		restBody += "<b>Supported Operations:</b>";
		if (needToken) {
			restBody += "<img src='" + contextRoot + "/image/lock.png'>";
		}
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot + "/rest/service/GeometryService/project'>Project</a>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot + "/rest/service/GeometryService/simplify'>Simplify</a>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot + "/rest/service/GeometryService/buffer'>Buffer</a>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/areasAndLengths'>Areas And Lengths</a>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot + "/rest/service/GeometryService/lengths'>Lengths</a>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot + "/rest/service/GeometryService/relation'>Relation</a>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot
				+ "/rest/service/GeometryService/labelPoints'>Label Points</a>";
		restBody += "<br/><br/>";

		result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Geometry Service");
		result += ServiceHTML.getNav(contextRoot).replace("${CATALOG}", " &gt; Geometry Service");
		result += ServiceHTML.getH2().replace("${TITLE}", "Geometry Service");
		result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
		result += ServiceHTML.getFooter();

		return result;
	}

}
