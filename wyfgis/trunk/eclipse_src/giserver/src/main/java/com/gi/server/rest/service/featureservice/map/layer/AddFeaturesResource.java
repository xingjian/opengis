package com.gi.server.rest.service.featureservice.map.layer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import org.geotools.feature.FeatureCollection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.identity.FeatureId;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.carto.Layer;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author wuyf
 * 
 */
@Path("/FeatureService/{serviceName}/{layerId}/addFeatures")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class AddFeaturesResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("features") String features) {
		return result(serviceName, layerId, token, f, features);
	}

	@POST
	public String postResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("features") String features) {
		return result(serviceName, layerId, token, f, features);
	}

	private String result(String serviceName, String layerId, String token,
			String f, String features) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token)
							&& !("html".equals(f) && features == null)) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				// Handle layerId
				int nLayerId = Integer.parseInt(layerId);

				MapDesc mapDesc = mapService.getMapDesc();
				boolean editable = mapDesc.getLayerInfo(nLayerId).isEditable();
				if(!editable){
					return null;
				}
					
				FeatureType featureType = null;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					Layer layer = instance.getMap().getLayer(nLayerId);
					featureType = layer.getMapLayer().getFeatureSource()
							.getSchema();
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Handle features
				FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = EsriJsonUtil
						.json2FeatureCollection(features,
								(SimpleFeatureType) featureType);

				instance = (MapServiceInstance) mapService.getMapServicePool()
						.checkoutIdleInstance();
				List<FeatureId> featureIds = null;
				try {
					Layer layer = instance.getMap().getLayer(nLayerId);
					featureIds = layer.add(featureCollection);
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureIds);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(serviceName, layerId,
							featureIds, token, features);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<FeatureId> featureIds) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (featureIds == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Add result is NULL.");
			} else {
				arrayTemp = new JSONArray();
				for (Iterator<FeatureId> itr = featureIds.iterator(); itr
						.hasNext();) {
					FeatureId featureId = itr.next();
					String fid = featureId.getID();
					String id = fid.substring(fid.lastIndexOf(".") + 1);
					int nId = Integer.valueOf(id);
					objTemp = new JSONObject();
					objTemp.put("objectId", nId);
					objTemp.put("success", nId >= 0 ? true : false);
					arrayTemp.put(objTemp);
				}
				obj.put("addResults", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String serviceName, String layerId,
			List<FeatureId> featureIds, String token, String features) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form action='" + contextRoot
					+ "/rest/service/FeatureService/" + serviceName + "/"
					+ layerId + "/addFeatures'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Features:</td><td><textarea name='features' rows='5' cols='40' >${FEATURES}</textarea></td></tr>");
			sb
					.append("<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='json' >JSON</option></select></td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' onclick='this.form.method = 'post';' value='Add Features (POST)' /></td></tr>");
			sb.append("</table></form>");

			if (features != null) {
				if (featureIds == null || featureIds.size() == 0) {
					sb
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				} else {
					int resultCount = featureIds.size();
					sb.append("<div style='color:#aaffaa'>Results number : "
							+ resultCount + "<br/></div><br/>");
					for (Iterator<FeatureId> itr = featureIds.iterator(); itr
							.hasNext();) {
						FeatureId featureId = itr.next();
						String fid = featureId.getID();
						String id = fid.substring(fid.lastIndexOf(".") + 1);
						sb.append("<br/>Feature ID: ");
						sb
								.append("<a href='" + contextRoot
										+ "/rest/service/FeatureService/"
										+ serviceName + "/" + layerId + "/"
										+ id + "'>" + id + "</a>");
						sb.append("<br/>");
					}
				}
			}

			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${FEATURES}", features == null ? ""
					: features);

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Add Features (" + serviceName + ")");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/FeatureService'>Feature Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/FeatureService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; <a href='" + contextRoot
							+ "/rest/service/FeatureService/" + serviceName
							+ "/" + layerId + "'>" + layerId
							+ "</a> &gt; Add Features");
			html.setHeader("Add Features Layer ID: " + layerId);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
