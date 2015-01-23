package com.gi.giserver.rest.service.mapservice;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.geotools.feature.FeatureCollection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.util.LayerUtil;
import com.gi.giserver.core.auth.Editor;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonGeometryUtil;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.EditorHTML;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */

@Path("/MapService/{mapName}/{layerId}/insert")
public class MapLayerInsertResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName, @PathParam("layerId") String layerId,
			@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("username") String username, @QueryParam("password") String password,
			@QueryParam("features") String features) {
		return result(mapName, layerId, token, f, username, password, features);
	}

	@POST
	public String postResult(@PathParam("mapName") String mapName, @PathParam("layerId") String layerId,
			@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("username") String username, @FormParam("password") String password,
			@FormParam("features") String features) {
		return result(mapName, layerId, token, f, username, password, features);
	}

	private synchronized String result(String mapName, String layerId, String token, String f, String username,
			String password, String features) {
		String result = null;

		try {
			boolean isSucceed = false;
			FeatureType featureType = null;

			Editor editor = new Editor(username, password);
			if (editor.isAuthorized()) {
				MapService mapService = ServiceManager.getMapService(mapName);
				if (mapService != null) {
					MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
					if (mapServiceDesc.isNeedToken()) {
						if (!TokenService.verifyToken(token) && !("html".equals(f) && features == null)) {
							return TokenService.TOKEN_INVALID_TIP;
						}
					}

					// Handle layerId
					int nLayerId = Integer.parseInt(layerId);

					// Handle features
					featureType = LayerUtil.getFeatureType(mapService.getLayer(nLayerId));
					FeatureCollection<SimpleFeatureType, SimpleFeature> featureCollection = EsriJsonUtil
							.json2FeatureCollection(features, (SimpleFeatureType)featureType);

					try {
						isSucceed = mapService.insert(nLayerId, featureCollection);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// Various out format
			if ("json".equals(f)) {
				Class<?> geometryType = null;
				if (featureType != null) {
					geometryType = featureType.getGeometryDescriptor().getType().getBinding();
				}
				result = this.generateJSONResult(editor, isSucceed, new JSONArray(features), geometryType);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(editor, mapName, layerId, isSucceed, token, features);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(Editor editor, boolean isSucceed, JSONArray features, Class<?> geometryType) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("about", "Powered by GIServer");
			if (!editor.isAuthorized()) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Please execute as editor.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.NO_EDITOR_AUTHORIZATION, details));
			} else {
				if (!isSucceed) {
					ArrayList<String> details = new ArrayList<String>();
					int featureCount = features.length();
					for (int i = 0; i < featureCount; i++) {
						details.add(features.getString(i));
					}
					obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.MAP_INSERT_FEATURES_ERROR, details));
				} else {
					obj.put("geometryType", EsriJsonGeometryUtil.geometryType2String(geometryType));
					obj.put("features", features);
				}
			}

			result = obj.toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Editor editor, String mapName, String layerId, boolean isSucceed, String token,
			String features) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();
			String rootURI = contextRoot + "/rest/service/MapService/" + mapName + "/" + layerId + "/";
			String thisURI = rootURI + "insert";

			String restBody = "";
			if (!editor.isAuthorized()) {
				restBody = EditorHTML.getLoginForm();
				restBody = restBody.replace("${POST_URI}", thisURI);
			} else {
				restBody += "<form><table style='border:1px solid #000000;'>";
				restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
				restBody += "<tr><td>Insert features: </td></tr>";
				restBody += "<tr><td><textarea name='features' rows='10' cols='40' >${FEATURES}</textarea></td></tr>";
				restBody += "<tr><td colspan='2' align='left'><input type='submit' value='Insert' /></td></tr>";
				restBody += "</table></form>";

				restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
				restBody = restBody.replace("${FEATURES}", features == null ? "" : features);

				if (!(features == null)) {
					if (!isSucceed) {
						restBody += "<div style='color:#ffaaaa'>Insert features failed.<br/></div>";
					} else {
						restBody += "<div style='color:#aaffaa'>Insert features succeed!<br/></div><br/>";
					}
				}
			}

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Insert Features");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot + "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/" + mapName + "'>" + mapName
							+ "</a> &gt; <a href='" + rootURI + "'>" + layerId + "</a> &gt; Insert Features");
			result += ServiceHTML.getH2().replace("${TITLE}", "Layer: " + layerId);
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
