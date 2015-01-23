package com.gi.giserver.rest.service.mapservice;

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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;

import com.gi.giengine.geometry.ProjectEngine;
import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.param.IdentifyParam;
import com.gi.giengine.map.query.FeatureResult;
import com.gi.giengine.map.query.IdentifyType;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.geometryservice.Project;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonGeometryUtil;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/MapService/{mapName}/identify")
public class MapIdentifyResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName, @QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f, @QueryParam("geometry") String geometry,
			@QueryParam("geometryType") String geometryType, @QueryParam("sr") String sr,
			@QueryParam("layers") String layers, @QueryParam("tolerance") @DefaultValue("0") String tolerance,
			@QueryParam("mapExtent") String mapExtent, @QueryParam("imageDisplay") String imageDisplay,
			@QueryParam("returnGeometry") @DefaultValue("true") String returnGeometry) {
		return result(mapName, token, f, geometry, geometryType, sr, layers, tolerance, mapExtent, imageDisplay,
				returnGeometry);
	}

	@POST
	public String postResult(@PathParam("mapName") String mapName, @FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f, @FormParam("geometry") String geometry,
			@FormParam("geometryType") String geometryType, @FormParam("sr") String sr,
			@FormParam("layers") String layers, @FormParam("tolerance") @DefaultValue("0") String tolerance,
			@FormParam("mapExtent") String mapExtent, @FormParam("imageDisplay") String imageDisplay,
			@FormParam("returnGeometry") @DefaultValue("true") String returnGeometry) {
		return result(mapName, token, f, geometry, geometryType, sr, layers, tolerance, mapExtent, imageDisplay,
				returnGeometry);
	}

	private synchronized String result(String mapName, String token, String f, String geometry, String geometryType,
			String sr, String layers, String tolerance, String mapExtent, String imageDisplay, String returnGeometry) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token) && !("html".equals(f) && geometry == null)) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				// Handle geometry
				Geometry identifyGeometry = EsriJsonGeometryUtil.json2Geometry(geometry);

				// Handle SRs
				String mapSR = mapDesc.getWkid();
				if (sr == null || "".equals(sr)) {
					sr = mapSR;
				}
				if (!sr.equals(mapSR)) {
					identifyGeometry = ProjectEngine.project(identifyGeometry, sr, mapSR);
				}

				// Handle layers
				IdentifyType identifyType = IdentifyType.TOP;
				ArrayList<String> layerIds = new ArrayList<String>();
				if (layers != null && !"".equals(layers)) {
					String type = null;
					String ids = layers;
					if (layers.indexOf(":") > 0) {
						String[] str = layers.split(":");
						type = str[0];
						ids = str[1];
						if (ids != null && !"".equals(ids)) {
							try {
								String[] strLayers = ids.split(",");
								int layerCount = strLayers.length;
								for (int i = 0; i < layerCount; i++) {
									layerIds.add(strLayers[i]);
								}
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} else {
						type = layers;
						int layerCount = mapDesc.getLayerDescs().size();
						for (int i = 0; i < layerCount; i++) {
							layerIds.add(String.valueOf(i));
						}
					}

					if ("top".equals(type.toLowerCase())) {
						identifyType = IdentifyType.TOP;
					} else if ("visible".equals(type.toLowerCase())) {
						identifyType = IdentifyType.VISIBLE;
					} else if ("all".equals(type.toLowerCase())) {
						identifyType = IdentifyType.ALL;
					}
				}

				// Handle tolerance
				int nTolerance = Integer.parseInt(tolerance);

				// Handle mapExtent
				Envelope env = null;
				if (mapExtent != null && !"".equals(mapExtent)) {
					String[] mapStrs = mapExtent.split(",");
					double xmin = Double.parseDouble(mapStrs[0]);
					double ymin = Double.parseDouble(mapStrs[1]);
					double xmax = Double.parseDouble(mapStrs[2]);
					double ymax = Double.parseDouble(mapStrs[3]);
					env = new Envelope(xmin, xmax, ymin, ymax);
					if (!sr.equals(mapSR)) {
						env = ProjectEngine.projectEnvelope(env, sr, mapSR);
					}
				}

				// Handle imageDisplay
				int width = 0;
				int height = 0;
				int dpi = 0;
				if (mapExtent != null && !"".equals(mapExtent)) {
					String[] imageStrs = imageDisplay.split(",");
					width = Integer.parseInt(imageStrs[0]);
					height = Integer.parseInt(imageStrs[1]);
					dpi = Integer.parseInt(imageStrs[2]);
				}

				// Handle returnGeometry
				boolean isReturnGeometry = Boolean.parseBoolean(returnGeometry);

				// Handle resolution
				 double resolutionX = env.getWidth() / width; 
				 double resolutionY = env.getHeight() / height; 
				 double resolution = Math.max(resolutionX, resolutionY);

				IdentifyParam identifyParam = new IdentifyParam();
				identifyParam.setGeometry(identifyGeometry);
				identifyParam.setIdentifyType(identifyType);
				identifyParam.setResolution(resolution);
				identifyParam.setTolerance(nTolerance);
				ArrayList<FeatureResult> featureResults = mapService.identify(layerIds,identifyParam);

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureResults, geometry, isReturnGeometry);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(mapName, featureResults, token, geometry, sr, layers, tolerance,
							mapExtent, imageDisplay);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(ArrayList<FeatureResult> featureResults, String geometry, boolean isReturnGeometry) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (geometry != null && featureResults == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Identify result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.MAP_IDENTIFY_ERROR, details));
			} else {
				arrayTemp = new JSONArray();
				Iterator<FeatureResult> iFeatureResult = featureResults.iterator();
				while (iFeatureResult.hasNext()) {
					FeatureResult featureResult = iFeatureResult.next();
					Feature feature = featureResult.getFeature();
					objTemp = EsriJsonUtil.feature2JSON(feature, isReturnGeometry);
					objTemp.put("layerId", featureResult.getLayerId());
					objTemp.put("layerName", featureResult.getLayerName());
					arrayTemp.put(objTemp);
				}
				obj.put("results", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String mapName, ArrayList<FeatureResult> featureResults, String token,
			String geometry, String sr, String layers, String tolerance, String mapExtent, String imageDisplay) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr valign='top'><td>Geometry Type:</td><td><select name='geometryType'><option value='esriGeometryPoint' >Point</option><option value='esriGeometryPolyline' >Polyline</option><option value='esriGeometryPolygon' >Polygon</option><option value='esriGeometryMultipoint' >Multipoint</option><option value='esriGeometryEnvelope' >Envelope</option></select></td></tr>";
			restBody += "<tr valign='top'><td>Geometry:</td><td><textarea name='geometry' rows='5' cols='40'>${GEOMETRY}</textarea></td></tr>";
			restBody += "<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>";
			restBody += "<tr valign='top'><td>Layers:</td><td><input type='text' name='layers' value='${LAYERS}'/></td></tr>";
			restBody += "<tr valign='top'><td>Tolerance:</td><td><input type='text' name='tolerance' value='${TOLERANCE}' /></td></tr>";
			restBody += "<tr valign='top'><td>Map Extent:</td><td><input type='text' name='mapExtent' value='${MAP_EXTENT}' size='50' /></td></tr>";
			restBody += "<tr valign='top'><td>Image Display:</td><td><input type='text' name='imageDisplay' value='${IMAGE_DISPLAY}' /></td></tr>";
			restBody += "<tr><td>Return Geometry: </td><td><input type='radio' name='returnGeometry' value='true' checked='true'  /> Yes &nbsp;<input type='radio' name='returnGeometry' value='false'  /> No</td></tr>";
			restBody += "<tr><td colspan='2' align='left'><input type='submit' value='Identify' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${GEOMETRY}", geometry == null ? "" : geometry);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${LAYERS}", layers == null ? "" : layers);
			restBody = restBody.replace("${TOLERANCE}", tolerance == null ? "" : tolerance);
			restBody = restBody.replace("${MAP_EXTENT}", mapExtent == null ? "" : mapExtent);
			restBody = restBody.replace("${IMAGE_DISPLAY}", imageDisplay == null ? "" : imageDisplay);

			if (!(geometry == null)) {
				if (featureResults == null || featureResults.size() == 0) {
					restBody += "<div style='color:#ffaaaa'>No results!<br/></div>";
				} else {
					int resultCount = featureResults.size();
					restBody += "<div style='color:#aaffaa'>Results number : " + resultCount + "<br/></div><br/>";
					if (resultCount > 10) {
						restBody += "<div style='color:#ffff00'>Only top 10 records will be printed<br/></div>";
					}
					int printCount = 0;
					for (Iterator<FeatureResult> it = featureResults.iterator(); it.hasNext() && printCount < 10;) {
						FeatureResult featureResult = it.next();
						restBody += "<div>";
						Feature feature = featureResult.getFeature();
						Geometry geo = (Geometry) feature.getDefaultGeometryProperty().getValue();
						if (geo != null) {
							String json = EsriJsonGeometryUtil.geometry2JSON(geo).toString();
							if (json.length() > 100) {
								restBody += json.substring(0, 99) + "...";
							} else {
								restBody += json;
							}
						}
						String attributes = "";
						String geometryFieldName = feature.getDefaultGeometryProperty().getName().getLocalPart();
						for (Iterator<Property> itr = feature.getProperties().iterator(); itr.hasNext();) {
							Property property = itr.next();
							String name = property.getDescriptor().getName().getLocalPart();
							if (!geometryFieldName.equals(name)) {
								Object value = property.getValue();
								attributes += "<br/>" + name + ":" + value.toString();
								if (attributes.length() > 100) {
									attributes += "<br/>...";
									break;
								}
							}
						}
						restBody += attributes;
						restBody += "<br/></div><tr><td><hr/></td></tr>";
						printCount++;
					}
				}
			}

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Identify");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot + "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/" + mapName + "'>" + mapName
							+ "</a> &gt; Identify");
			result += ServiceHTML.getH2().replace("${TITLE}", "Identify");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
