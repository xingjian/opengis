package com.gi.server.rest.service.mapservice.map;

import java.util.ArrayList;
import java.util.Iterator;

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
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.carto.FeatureResult;
import com.gi.engine.carto.FindParam;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author wuyf
 * 
 */
@Path("/MapService/{serviceName}/find")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class FindResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(
			@PathParam("serviceName") String serviceName,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("searchText") String searchText,
			@QueryParam("contains") @DefaultValue("true") String contains,
			@QueryParam("searchFields") String searchFields,
			@QueryParam("sr") String sr,
			@QueryParam("layers") String layers,
			@QueryParam("returnGeometry") @DefaultValue("true") String returnGeometry) {
		return result(serviceName, token, f, searchText, contains,
				searchFields, sr, layers, returnGeometry);
	}

	@POST
	public String postResult(
			@PathParam("serviceName") String serviceName,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("searchText") String searchText,
			@FormParam("contains") @DefaultValue("true") String contains,
			@FormParam("searchFields") String searchFields,
			@FormParam("sr") String sr,
			@FormParam("layers") String layers,
			@FormParam("returnGeometry") @DefaultValue("true") String returnGeometry) {
		return result(serviceName, token, f, searchText, contains,
				searchFields, sr, layers, returnGeometry);
	}

	private String result(String serviceName, String token, String f,
			String searchText, String contains, String searchFields, String sr,
			String layers, String returnGeometry) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token)
							&& !("html".equals(f) && searchText == null)) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				String mapSR = mapDesc.getWkid();

				// Handle contains
				boolean isContains = Boolean.parseBoolean(contains);

				// Handle searchFields
				String[] strSearchFields = null;
				if (searchFields != null && !"".equals(searchFields)) {
					strSearchFields = searchFields.split(",");
				}

				// Handle SRs
				if (sr == null || "".equals(sr)) {
					sr = mapSR;
				}

				// Handle layers
				String[] layerIds = null;
				if (layers != null && !"".equals(layers)) {
					layerIds = layers.split(",");
				}
				ArrayList<String> layerIdArray = null;
				if (layerIds != null) {
					layerIdArray = new ArrayList<String>();
					int count = layerIds.length;
					for (int i = 0; i < count; i++) {
						layerIdArray.add(layerIds[i]);
					}
				}

				// Handle returnGeometry
				boolean isReturnGeometry = Boolean.parseBoolean(returnGeometry);

				FindParam findParam = new FindParam();
				findParam.setSearchText(searchText);
				findParam.setContains(isContains);
				findParam.setSearchFields(strSearchFields);

				ArrayList<FeatureResult> featureResults = null;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					featureResults = instance.find(layerIdArray, findParam);
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureResults,
							searchText, isReturnGeometry, mapSR, sr);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(serviceName,
							featureResults, token, searchText, searchFields,
							sr, layers, mapSR);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(ArrayList<FeatureResult> featureResults,
			String searchText, boolean isReturnGeometry, String mapSR,
			String outSR) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;
			String lowSearchText = searchText.toLowerCase();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (searchText != null && featureResults == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Find result is NULL.");
			} else {
				arrayTemp = new JSONArray();
				for (Iterator<FeatureResult> it = featureResults.iterator(); it
						.hasNext();) {
					FeatureResult featureResult = it.next();
					Feature feature = featureResult.getFeature();
					GeometryAttribute geoAttr = feature
							.getDefaultGeometryProperty();
					Geometry geo = (Geometry) geoAttr.getValue();
					if (geo != null) {
						geo = GeometryToolkit.project(geo, mapSR, outSR);
						geoAttr.setValue(geo);
						feature.setDefaultGeometryProperty(geoAttr);
					}
					objTemp = EsriJsonUtil.feature2JSON(feature,
							isReturnGeometry);
					objTemp.put("layerId", featureResult.getLayerId());
					objTemp.put("layerName", featureResult.getLayer()
							.getMapLayer().getTitle());

					String geoFieldName = feature.getDefaultGeometryProperty()
							.getDescriptor().getLocalName();
					for (Iterator<Property> itr = feature.getProperties()
							.iterator(); itr.hasNext();) {
						Property pro = itr.next();
						String fieldName = pro.getDescriptor().getName()
								.getLocalPart();
						if (!fieldName.equals(geoFieldName)) {
							String proValue = pro.getValue().toString();
							if (proValue.toLowerCase().contains(lowSearchText)) {
								objTemp.put("foundFieldName", fieldName);
								objTemp.put("value", proValue);
								break;
							}
						}
					}
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

	private String generateHTMLResult(String serviceName,
			ArrayList<FeatureResult> featureResults, String token,
			String searchText, String searchFields, String sr, String layers,
			String mapSR) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form action='" + contextRoot
					+ "/rest/service/MapService/" + serviceName
					+ "/find'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Search Text:</td><td><input type='text' name='searchText' value='${SEARCH_TEXT}' size='50' /></td></tr>");
			sb
					.append("<tr><td>Contains: </td><td><input type='radio' name='contains' value='true' checked='true'  /> Yes &nbsp;<input type='radio' name='contains' value='false'  /> No</td>");
			sb
					.append("<tr><td>Search Fields:</td><td><input type='text' name='searchFields' value='${SEARCH_FIELDS}' size='50' /></td></tr>");
			sb
					.append("<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>");
			sb
					.append("<tr><td>Layers:</td><td><input type='text' name='layers' value='${LAYERS}'/></td></tr>");
			sb
					.append("<tr><td>Return Geometry: </td><td><input type='radio' name='returnGeometry' value='true' checked='true'  /> Yes &nbsp;<input type='radio' name='returnGeometry' value='false' /> No</td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' value='Find (GET)' /><input type='submit' onclick='this.form.method = 'post';' value='Find (POST)' /></td></tr>");
			sb.append("</table></form>");

			if (!(searchText == null)) {
				if (featureResults == null || featureResults.size() == 0) {
					sb
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				} else {
					int resultCount = featureResults.size();
					sb.append("<div style='color:#aaffaa'>Results number : "
							+ resultCount + "<br/></div><br/>");
					if (resultCount > 10) {
						sb
								.append("<div style='color:#ffff00'>Only top 10 records will be printed<br/></div>");
					}
					int printCount = 0;
					for (Iterator<FeatureResult> it = featureResults.iterator(); it
							.hasNext()
							&& printCount < 10;) {
						FeatureResult featureResult = it.next();
						sb.append("<div>");
						Feature feature = featureResult.getFeature();
						GeometryAttribute geoAttr = feature
								.getDefaultGeometryProperty();
						Geometry geo = (Geometry) geoAttr.getValue();
						if (geo != null) {
							geo = GeometryToolkit.project(geo, mapSR, sr);
							geoAttr.setValue(geo);
							feature.setDefaultGeometryProperty(geoAttr);
							String json = EsriJsonUtil.geometry2JSON(geo)
									.toString();
							if (json.length() > 100) {
								sb.append(json.substring(0, 99) + "...");
							} else {
								sb.append(json);
							}
						}
						String attributes = "";
						String geometryField = feature
								.getDefaultGeometryProperty().getName()
								.getLocalPart();
						for (Iterator<Property> itr = feature.getProperties()
								.iterator(); itr.hasNext();) {
							Property property = itr.next();
							String name = property.getDescriptor().getName()
									.getLocalPart();
							if (!geometryField.equals(name)) {
								Object value = property.getValue();
								attributes += "<br/>" + name + ":"
										+ value.toString();
								if (attributes.length() > 100) {
									attributes += "<br/>...";
									break;
								}
							}
						}
						sb.append(attributes);
						sb.append("<br/></div><tr><td><hr/></td></tr>");
						printCount++;
					}
				}
			}

			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SEARCH_TEXT}",
					searchText == null ? "" : searchText);
			restBody = restBody.replace("${SEARCH_FIELDS}",
					searchFields == null ? "" : searchFields);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${LAYERS}", layers == null ? ""
					: layers);

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Find (" + serviceName + "}");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; Find");
			html.setHeader("Find");
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
