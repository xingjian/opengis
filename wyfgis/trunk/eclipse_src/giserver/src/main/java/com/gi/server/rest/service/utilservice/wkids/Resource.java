package com.gi.server.rest.service.utilservice.wkids;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.gi.engine.util.VersionUtil;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/UtilService/wkids")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class Resource {

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
			ArrayList<String> wkids = SpatialReferenceManager
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

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
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

			StringBuilder sb = new StringBuilder();
			if (wkids != null) {
				sb.append("<ul>");
				Iterator<String> it = wkids.iterator();
				while (it.hasNext()) {
					String wkid = it.next();
					sb.append("<li><a href='" + contextRoot
							+ "/rest/service/UtilService/wkids/" + wkid + "'>"
							+ wkid + "</a></li>");
				}
				sb.append("</ul>");
			}

			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("WKIDs");
			html.setContextRoot(contextRoot);
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/UtilService'>Utility Service</a> &gt; WKIDs");
			html.setHeader("Supported WKIDs");
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
