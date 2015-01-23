package com.gi.server.rest.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.rest.html.ServiceHTML;



/**
 * @author wuyf
 *
 */
@Path("/")
public class Resource {

	@Context
	ServletContext context;

	@GET
	@Produces({"text/html", "application/json"})
	public String getResult(@QueryParam("f") @DefaultValue("html") String f) {
		return result(f);
	}

	@POST
	@Produces({"text/html", "application/json"})
	public String postResult(@FormParam("f") @DefaultValue("html") String f) {
		return result(f);
	}

	private String result(String f) {
		String result = null;

		try {
			HashMap<String, MapService> mapServices = ServiceManager.getMapServices();

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(mapServices);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(mapServices);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(HashMap<String, MapService> mapServices) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;
			JSONObject objTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			arrayTemp = new JSONArray();
			for(Iterator<Entry<String, MapService>> itr=mapServices.entrySet().iterator();itr.hasNext();){
				Entry<String, MapService> entry = itr.next();
				String name = entry.getKey();
				MapService mapService = entry.getValue();
				if (mapService.isStarted()) {
					objTemp = new JSONObject();
					objTemp.put("name", name);
					objTemp.put("needToken", mapService.getMapServiceDesc().isNeedToken());
					arrayTemp.put(objTemp);
				}
			}
			obj.put("mapServices", arrayTemp);

			obj.put("geometryService", "internal");
			obj.put("utilService", "internal");
			obj.put("tokenService", "internal");

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(HashMap<String, MapService> mapServices) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();

			sb.append("<b><a href='" + contextRoot + "/rest/service/MapService'>Map Service</a>:</b>");
			sb.append("<ul>");
			for(Iterator<Entry<String, MapService>> itr=mapServices.entrySet().iterator();itr.hasNext();){
				Entry<String, MapService> entry = itr.next();
				String name = entry.getKey();
				MapService mapService = entry.getValue();
				if (mapService.isStarted()) {
					sb.append("<li><a href='" + contextRoot + "/rest/service/MapService/" + name + "'>"
							+ name + "</a>");
					if (mapService.getMapServiceDesc().isNeedToken()) {
						sb.append("<img src='" + contextRoot + "/image/lock.png'>");
					}
					sb.append("</li>");
				}
			}
			sb.append("</ul><br/>");

			sb.append("<b><a href='" + contextRoot + "/rest/service/FeatureService'>Feature Service</a>:</b>");
			sb.append("<ul>");
			for(Iterator<Entry<String, MapService>> itr=mapServices.entrySet().iterator();itr.hasNext();){
				Entry<String, MapService> entry = itr.next();
				String name = entry.getKey();
				MapService mapService = entry.getValue();
				if (mapService.isStarted()) {
					sb.append("<li><a href='" + contextRoot + "/rest/service/FeatureService/" + name + "'>"
							+ name + "</a>");
					if (mapService.getMapServiceDesc().isNeedToken()) {
						sb.append("<img src='" + contextRoot + "/image/lock.png'>");
					}
					sb.append("</li>");
				}
			}
			sb.append("</ul><br/>");

			sb.append("<b><a href='" + contextRoot
					+ "/rest/service/GeometryService'>Geometry Service</a> (Internal)</b>");
			sb.append("<br/><br/>");

			sb.append("<b><a href='" + contextRoot
					+ "/rest/service/UtilService'>Utility Service</a> (Internal)</b>");
			sb.append("<br/><br/>");

			sb.append("<b><a href='" + contextRoot
					+ "/rest/service/TokenService'>Token Service</a> (Internal)</b>");
			sb.append("<br/><br/>");

			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Service Home");
			html.setCatalog("");
			html.setHeader("Service Home");
			html.setRestBody(restBody);
			
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
