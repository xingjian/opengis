package com.gi.giserver.rest.service;

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

import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/")
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
			MapService[] mapServices = ServiceManager.getMapServices();

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

	private String generateJSONResult(MapService[] mapServices) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;
			JSONObject objTemp = null;

			obj.put("about", "Powered by GIServer");

			arrayTemp = new JSONArray();
			int count = mapServices.length;
			for (int i = 0; i < count; i++) {
				MapService mapService = mapServices[i];
				if (mapService.isStarted()) {
					objTemp = new JSONObject();
					objTemp.put("name", mapService.getMapDesc().getMapName());
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

	private String generateHTMLResult(MapService[] mapServices) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sbRestBody = new StringBuilder();

			sbRestBody.append("<b><a href='" + contextRoot + "/rest/service/MapService'>Map Service</a>:</b>");
			sbRestBody.append("<ul>");
			int count = mapServices.length;
			for (int i = 0; i < count; i++) {
				MapService mapService = mapServices[i];
				if (mapService.isStarted()) {
					String mapName = mapService.getMapDesc().getMapName();
					sbRestBody.append("<li><a href='" + contextRoot + "/rest/service/MapService/" + mapName + "'>"
							+ mapName + "</a>");
					if (mapService.getMapServiceDesc().isNeedToken()) {
						sbRestBody.append("<img src='" + contextRoot + "/image/lock.png'>");
					}
					sbRestBody.append("</li>");
				}
			}
			sbRestBody.append("</ul><br/>");

			sbRestBody.append("<b><a href='" + contextRoot
					+ "/rest/service/GeometryService'>Geometry Service</a> (Internal)</b>");
			sbRestBody.append("<br/><br/>");

			sbRestBody.append("<b><a href='" + contextRoot
					+ "/rest/service/UtilService'>Utility Service</a> (Internal)</b>");
			sbRestBody.append("<br/><br/>");

			sbRestBody.append("<b><a href='" + contextRoot
					+ "/rest/service/TokenService'>Token Service</a> (Internal)</b>");
			sbRestBody.append("<br/><br/>");

			String restBody = sbRestBody.toString();

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Service Home");
			result += ServiceHTML.getNav(contextRoot).replace("${CATALOG}", "");
			result += ServiceHTML.getH2().replace("${TITLE}", "Home");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
