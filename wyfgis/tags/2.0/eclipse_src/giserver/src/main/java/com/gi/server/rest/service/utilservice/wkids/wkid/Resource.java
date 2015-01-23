package com.gi.server.rest.service.utilservice.wkids.wkid;

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

import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.gi.engine.util.VersionUtil;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/UtilService/wkids/{wkid}")
public class Resource {

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
			CoordinateReferenceSystem crs = SpatialReferenceManager.wkidToCRS(
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
			
			obj.put("GIServerVersion", VersionUtil.getCurrentversion());

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

			StringBuilder sb = new StringBuilder();
			sb.append("<br/>");
			sb.append("<b>WKID:</b><br/>" + wkid);
			sb.append("<br/><br/>");
			sb.append("<b>WKT:</b><br/>" + wkt);

			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("WKID");
			html.setCatalog(" &gt; <a href='"
					+ contextRoot
					+ "/rest/service/UtilService'>Utility Service</a> &gt; <a href='"
					+ contextRoot
					+ "/rest/service/UtilService/wkids'>WKIDs</a> &gt; "+wkid);
			html.setHeader("Spatial Reference");
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
