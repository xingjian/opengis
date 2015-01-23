package com.gi.giserver.rest.service.mapservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;

import com.gi.giengine.map.desc.MapDesc;
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
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Wu Yongfeng
 * 
 */

@Path("/MapService/{mapName}/{layerId}/modify")
public class MapLayerModifyResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName, @PathParam("layerId") String layerId,
			@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("username") String username, @QueryParam("password") String password,
			@QueryParam("where") String where, @QueryParam("geometry") String geometry,
			@QueryParam("attributes") String attributes) {
		return result(mapName, layerId, f, username, password, token, where, geometry, attributes);
	}

	@POST
	public String postResult(@PathParam("mapName") String mapName, @PathParam("layerId") String layerId,
			@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("username") String username, @FormParam("password") String password,
			@FormParam("where") String where, @FormParam("geometry") String geometry,
			@FormParam("attributes") String attributes) {
		return result(mapName, layerId, f, username, password, token, where, geometry, attributes);
	}

	private synchronized String result(String mapName, String layerId, String f, String username, String password,
			String token, String where, String geometry, String attributes) {
		String result = null;

		try {
			Feature[] modifyFeatures = null;

			Editor editor = new Editor(username, password);
			if (editor.isAuthorized()) {
				MapService mapService = ServiceManager.getMapService(mapName);
				if (mapService != null) {
					MapDesc mapDesc = mapService.getMapDesc();
					MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
					if (mapServiceDesc.isNeedToken()) {
						if (!TokenService.verifyToken(token)
								&& !("html".equals(f) && where == null && geometry == null && attributes == null)) {
							return TokenService.TOKEN_INVALID_TIP;
						}
					}

					// Handle layerId
					int nLayerId = Integer.parseInt(layerId);

					// Handle geometry
					Geometry geo = EsriJsonGeometryUtil.json2Geometry(geometry);

					// Handle attributes
					HashMap<String, Object> attributesMap = null;
					try {
						if (attributes != null && !"".equals(attributes.trim())) {
							attributesMap = new HashMap<String, Object>();
							JSONObject objAttributes = new JSONObject(attributes);
							Iterator<?> itKeys = objAttributes.keys();
							while (itKeys.hasNext()) {
								String key = itKeys.next().toString();
								Object value = objAttributes.get(key);
								attributesMap.put(key, value);
							}
						}
					} catch (Exception ex) {
						attributesMap = null;
					}

					try {
						modifyFeatures = mapService.modify(nLayerId, where, geo, attributesMap);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(editor, modifyFeatures, where, geometry, attributes);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(editor, mapName, layerId, modifyFeatures, token, where, geometry,
						attributes);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(Editor editor, Feature[] modifyFeatures, String where, String geometry,
			String attributes) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONObject objTemp2 = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (!editor.isAuthorized()) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Please execute as editor.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.NO_EDITOR_AUTHORIZATION, details));
			} else {
				if (modifyFeatures == null || modifyFeatures.length == 0) {
					ArrayList<String> details = new ArrayList<String>();
					details.add("Nothing was modified.");
					obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.MAP_MODIFY_FEATURES_ERROR, details));
				} else {
					Feature feature0 = modifyFeatures[0];
					Geometry geo0 = (Geometry) feature0.getDefaultGeometryProperty().getValue();
					obj.put("geometryType", EsriJsonGeometryUtil.geometryType2String(geo0.getClass()));
					String geometryField =feature0.getDefaultGeometryProperty().getName().getLocalPart();

					ArrayList<String> fields = new ArrayList<String>();
					for (Iterator<Property> itr = feature0.getProperties().iterator(); itr.hasNext();) {
						Property property = itr.next();
						String name = property.getDescriptor().getName().getLocalPart();
						if (!geometryField.equals(name)) {
							fields.add(name);
						}
					}

					arrayTemp = new JSONArray();
					int featureCount = modifyFeatures.length;
					for (int i = 0; i < featureCount; i++) {
						Feature feature = modifyFeatures[i];
						objTemp = new JSONObject();

						Geometry geo = (Geometry) feature.getDefaultGeometryProperty().getValue();
						objTemp.put("geometry", EsriJsonGeometryUtil.geometry2JSON(geo));

						objTemp2 = new JSONObject();
						for (Iterator<Property> itr = feature.getProperties().iterator(); itr.hasNext();) {
							Property property = itr.next();
							String name = property.getDescriptor().getName().getLocalPart();
							if (!geometryField.equals(name)) {
								Object value = property.getValue();
								objTemp2.put(name, value);
							}
						}

						objTemp.put("attributes", objTemp2);
						arrayTemp.put(objTemp);
					}
					obj.put("features", arrayTemp);
				}
			}

			result = obj.toString();
		} catch (JSONException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Editor editor, String mapName, String layerId, Feature[] modifyFeatures,
			String token, String where, String geometry, String attributes) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();
			String rootURI = contextRoot + "/rest/service/MapService/" + mapName + "/" + layerId + "/";
			String thisURI = rootURI + "modify";

			String restBody = "";
			if (!editor.isAuthorized()) {
				restBody = EditorHTML.getLoginForm();
				restBody = restBody.replace("${POST_URI}", thisURI);
			} else {
				restBody += "<form><table style='border:1px solid #000000;'>";
				restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
				restBody += "<tr><td>Modify features: </td></tr>";
				restBody += "<tr><td>Where: </td><td><input type='text' size='50' name='where' value='${WHERE}' /></td></tr>";
				restBody += "<tr><td>Geometry: </td><td><textarea name='geometry' rows='5' cols='40' >${GEOMETRY}</textarea></td></tr>";
				restBody += "<tr><td>Attributes: </td><td><textarea name='attributes' rows='5' cols='40' >${ATTRIBUTES}</textarea></td></tr>";
				restBody += "<tr><td colspan='2' align='left'><input type='submit' value='Modify' /></td></tr>";
				restBody += "</table></form>";

				restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
				restBody = restBody.replace("${WHERE}", where == null ? "" : where.replaceAll("'", "&#39;"));
				restBody = restBody.replace("${GEOMETRY}", geometry == null ? "" : geometry);
				restBody = restBody.replace("${ATTRIBUTES}", attributes == null ? "" : attributes);

				if (!(where == null)) {
					if (modifyFeatures == null || modifyFeatures.length == 0) {
						restBody += "<div style='color:#ffaaaa'>Nothing was modified!<br/></div>";
					} else {
						int resultCount = modifyFeatures.length;
						restBody += "<div style='color:#aaffaa'>Modified features number : " + resultCount
								+ "<br/></div><br/>";
						if (resultCount > 10) {
							restBody += "<div style='color:#ffff00'>Only top 10 records will be printed<br/></div>";
						}
						String geometryField = modifyFeatures[0].getDefaultGeometryProperty().getDescriptor()
								.getLocalName();
						int printCount = 0;
						for (int i = 0; i < resultCount; i++) {
							Feature feature = modifyFeatures[i];
							restBody += "<div>";

							Geometry geo = (Geometry) feature.getDefaultGeometryProperty().getValue();
							if (geo != null) {
								String json = EsriJsonGeometryUtil.geometry2JSON(geo).toString();
								restBody += "<br/>Geometry: ";
								if (json.length() > 100) {
									restBody += json.substring(0, 99) + "...";
								} else {
									restBody += json;
								}
							}

							String htmlAttributes = "";
							for (Iterator<Property> itr = feature.getProperties().iterator(); itr.hasNext();) {
								Property property = itr.next();
								String name = property.getDescriptor().getName().getLocalPart();
								if (!geometryField.equals(name)) {
									Object value = property.getValue();
									value = value == null ? "" : value;
									htmlAttributes += "<br/>" + name + ": " + value.toString();
									if (htmlAttributes.length() > 100) {
										htmlAttributes += "<br/>...";
										break;
									}
								}
							}
							restBody += htmlAttributes;

							restBody += "<br/></div><tr><td><hr/></td></tr>";
							printCount++;
						}
					}
				}
			}

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Modify Features");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					"&gt; <a href='" + contextRoot + "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/" + mapName + "'>" + mapName
							+ "</a> &gt; <a href='" + rootURI + "'>" + layerId + "</a> &gt; Modify Features");
			result += ServiceHTML.getH2().replace("${TITLE}", "Layer: " + layerId);
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
