package com.gi.giserver.rest.service.utilservice;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.giengine.sr.SpatialReferenceEngine;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/UtilService/wkid/{wkid}")
public class WkidResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("f") @DefaultValue("html") String f,
			@PathParam("wkid") String wkid) {
		return result(f, wkid);
	}

	@POST
	public String postResult(@FormParam("f") @DefaultValue("html") String f,
			@PathParam("wkid") String wkid) {
		return result(f, wkid);
	}

	private String result(String f, String wkid) {
		String result = null;

		try {
			CoordinateReferenceSystem crs = SpatialReferenceEngine.wkidToCRS(
					wkid, false);
			String wkt = crs.toWKT();

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(wkid, wkt);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(wkid, wkt);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(String wkid, String wkt) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("about", "Powered by GIServer");
			obj.put("wkid", wkid);
			obj.put("wkt", wkt);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String wkid, String wkt) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();
			String restBody = "";

			StringBuilder sb = new StringBuilder();
			sb.append("<br/>");
			sb.append("<b>WKID:</b><br/>" + wkid);
			sb.append("<br/><br/>");
			sb.append("<b>WKT:</b><br/>" + wkt);

			restBody += sb.toString();

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}",
					"WKID");
			result += ServiceHTML
					.getNav(contextRoot)
					.replace(
							"${CATALOG}",
							" &gt; <a href='"
									+ contextRoot
									+ "/rest/service/UtilService'>Utility Service</a> &gt; WKID");
			result += ServiceHTML.getH2().replace("${TITLE}", "Referencing");
			result += ServiceHTML.getRestBody()
					.replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
