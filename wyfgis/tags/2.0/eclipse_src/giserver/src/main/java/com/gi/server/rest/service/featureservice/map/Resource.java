package com.gi.server.rest.service.featureservice.map;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.carto.LayerInfo;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author wuyf
 * 
 */
@Path("/FeatureService/{serviceName}")
public class Resource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(@PathParam("serviceName") String serviceName,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f) {
		return result(serviceName, token, f);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(@PathParam("serviceName") String serviceName,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f) {
		return result(serviceName, token, f);
	}

	private String result(String serviceName, String token, String f) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token) && !("html".equals(f))) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(mapService);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(mapService);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(MapService mapService) {
		String result = null;

		try {
			MapDesc mapDesc = mapService.getMapDesc();
			String mapName = mapDesc.getName();

			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			obj.put("name", mapName);

			arrayTemp = new JSONArray();
			for (int i = 0, count = mapDesc.getLayerInfos().size(); i < count; i++) {
				LayerInfo layerInfo = mapDesc.getLayerInfo(i);
				if (layerInfo.isEditable()) {
					objTemp = new JSONObject();
					objTemp.put("id", i);
					objTemp.put("name", layerInfo.getName());
					arrayTemp.put(objTemp);
				}
			}
			obj.put("layers", arrayTemp);

			objSR = new JSONObject();
			objSR.put("wkid", mapDesc.getWkid());
			obj.put("spatialReference", objSR);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(MapService mapService) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			MapDesc mapDesc = mapService.getMapDesc();
			String mapName = mapDesc.getName();
			String serviceName = mapService.getServiceName();

			StringBuilder sb = new StringBuilder();
			sb.append("<b>Layers: </b>");
			sb.append("<ul>");
			int layerCount = mapDesc.getLayerInfos().size();
			for (int i = 0; i < layerCount; i++) {
				LayerInfo layerInfo = mapDesc.getLayerInfo(i);
				sb.append("<li><a href='" + contextRoot
						+ "/rest/service/FeatureService/" + serviceName + "/" + i
						+ "'>" + layerInfo.getName() + "</a>(" + i + ")</li>");
				sb.append(layerInfo.isEditable() ? "[editable]" : "");
			}
			sb.append("</ul>");

			sb.append("<br/><br/>");

			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle(serviceName);
			html.setCatalog(" &gt; <a href='" + contextRoot
					+ "/rest/service/FeatureService'>Feature Service</a> &gt; "
					+ serviceName);
			html.setHeader(mapName);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
