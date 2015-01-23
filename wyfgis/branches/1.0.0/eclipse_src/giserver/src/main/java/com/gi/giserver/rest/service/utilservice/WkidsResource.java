package com.gi.giserver.rest.service.utilservice;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.giengine.sr.SpatialReferenceEngine;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/UtilService/wkids")
public class WkidsResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f) {
		return result(f);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f) {
		return result(f);
	}

	private String result(String f) {
		String result = null;

		try {
			ArrayList<String> wkids = SpatialReferenceEngine
					.getAllAvalibleWkids();

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(wkids);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(wkids);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(ArrayList<String> wkids) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (wkids != null) {
				arrayTemp = new JSONArray();
				Iterator<String> it = wkids.iterator();
				while (it.hasNext()) {
					String wkid = it.next();
					arrayTemp.put(wkid);
				}
				obj.put("wkids", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(ArrayList<String> wkids) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();
			String restBody = "";

			StringBuilder sb = new StringBuilder();
			if (wkids != null) {
				sb.append("<ul>");
				Iterator<String> it = wkids.iterator();
				while (it.hasNext()) {
					String wkid = it.next();
					sb.append("<li><a href='" + contextRoot
							+ "/rest/service/UtilService/wkid/" + wkid + "'>"
							+ wkid + "</a></li>");
				}
				sb.append("</ul>");
			}

			restBody += sb.toString();

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}",
					"WKIDs");
			result += ServiceHTML
					.getNav(contextRoot)
					.replace(
							"${CATALOG}",
							" &gt; <a href='"
									+ contextRoot
									+ "/rest/service/UtilService'>Utility Service</a> &gt; WKIDs");
			result += ServiceHTML.getH2()
					.replace("${TITLE}", "Supported WKIDs");
			result += ServiceHTML.getRestBody()
					.replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
