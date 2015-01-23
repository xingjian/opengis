package com.gi.giserver.rest.service.utilservice;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONException;
import org.json.JSONObject;

import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/UtilService")
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
			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult();
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult() {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			obj.put("about", "Powered by GIServer");

			result = obj.toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult() {
		String result = "";

		String contextRoot = context.getContextPath();

		String restBody = "";
		restBody += "<b>Utilities:</b>";
		restBody += "&nbsp;&nbsp;<a href='" + contextRoot + "/rest/service/UtilService/wkids'>Supported WKIDs</a>";
		restBody += "<br/><br/>";

		result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Utility Service");
		result += ServiceHTML.getNav(contextRoot).replace("${CATALOG}", " &gt; Utility Service");
		result += ServiceHTML.getH2().replace("${TITLE}", "Utility Service");
		result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
		result += ServiceHTML.getFooter();

		return result;
	}

}
