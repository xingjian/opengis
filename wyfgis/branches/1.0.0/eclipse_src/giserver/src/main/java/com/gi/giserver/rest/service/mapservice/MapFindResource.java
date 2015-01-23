package com.gi.giserver.rest.service.mapservice;

import java.util.ArrayList;
import java.util.Iterator;

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

import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.param.FindParam;
import com.gi.giengine.map.query.FeatureResult;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.core.util.error.ServiceError;
import com.gi.giserver.core.util.json.EsriJsonGeometryUtil;
import com.gi.giserver.core.util.json.EsriJsonUtil;
import com.gi.giserver.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/MapService/{mapName}/find")
public class MapFindResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName, @QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("searchText") String searchText, @QueryParam("contains") @DefaultValue("true") String contains,
			@QueryParam("searchFields") String searchFields, @QueryParam("sr") String sr,
			@QueryParam("layers") String layers,
			@QueryParam("returnGeometry") @DefaultValue("true") String returnGeometry) {
		return result(mapName,token , f, searchText, contains, searchFields, sr, layers, returnGeometry);
	}

	@POST
	public String postResult(@PathParam("mapName") String mapName, @FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("searchText") String searchText, @FormParam("contains") @DefaultValue("true") String contains,
			@FormParam("searchFields") String searchFields, @FormParam("sr") String sr,
			@FormParam("layers") String layers, @FormParam("returnGeometry") @DefaultValue("true") String returnGeometry) {
		return result(mapName, token, f, searchText, contains, searchFields, sr, layers, returnGeometry);
	}

	private synchronized String result(String mapName, String token, String f, String searchText, String contains,
			String searchFields, String sr, String layers, String returnGeometry) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token) && !("html".equals(f) && searchText==null)) {
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
				if(layerIds!=null){
					layerIdArray = new ArrayList<String>();
					int count = layerIds.length;
					for(int i=0; i<count; i++){
						layerIdArray.add(layerIds[i]);
					}
				}

				// Handle returnGeometry
				boolean isReturnGeometry = Boolean.parseBoolean(returnGeometry);

				FindParam findParam = new FindParam();
				findParam.setSearchText(searchText);
				findParam.setContains(isContains);
				findParam.setSearchFields(strSearchFields);
				ArrayList<FeatureResult> featureResults = mapService.find(layerIdArray, findParam);

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureResults, searchText, isReturnGeometry, mapSR, sr);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(mapName, featureResults, token, searchText, searchFields, sr, layers);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(ArrayList<FeatureResult> featureResults, String searchText,
			boolean isReturnGeometry, String mapSR, String outSR) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (searchText != null && featureResults == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Find result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.MAP_FIND_ERROR, details));
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

	private String generateHTMLResult(String mapName, ArrayList<FeatureResult> featureResults, String token, String searchText,
			String searchFields, String sr, String layers) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr><td>Search Text:</td><td><input type='text' name='searchText' value='${SEARCH_TEXT}' size='50' /></td></tr>";
			restBody += "<tr><td>Contains: </td><td><input type='radio' name='contains' value='true' checked='true'  /> Yes &nbsp;<input type='radio' name='contains' value='false'  /> No</td>";
			restBody += "<tr><td>Search Fields:</td><td><input type='text' name='searchFields' value='${SEARCH_FIELDS}' size='50' /></td></tr>";
			restBody += "<tr><td>Spatial Reference (WKID):</td><td><input type='text' name='sr' value='${SR}' /></td></tr>";
			restBody += "<tr><td>Layers:</td><td><input type='text' name='layers' value='${LAYERS}'/></td></tr>";
			restBody += "<tr><td>Return Geometry: </td><td><input type='radio' name='returnGeometry' value='true' checked='true'  /> Yes &nbsp;<input type='radio' name='returnGeometry' value='false' /> No</td></tr>";
			restBody += "<tr><td colspan='2' align='left'><input type='submit' value='Find' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${SEARCH_TEXT}", searchText == null ? "" : searchText);
			restBody = restBody.replace("${SEARCH_FIELDS}", searchFields == null ? "" : searchFields);
			restBody = restBody.replace("${SR}", sr == null ? "" : sr);
			restBody = restBody.replace("${LAYERS}", layers == null ? "" : layers);

			if (!(searchText == null)) {
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
						String geometryField = feature.getDefaultGeometryProperty().getName().getLocalPart();
						for (Iterator<Property> itr = feature.getProperties().iterator(); itr.hasNext();) {
							Property property = itr.next();
							String name = property.getDescriptor().getName().getLocalPart();
							if (!geometryField.equals(name)) {
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

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Find");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot + "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/" + mapName + "'>" + mapName + "</a> &gt; Find");
			result += ServiceHTML.getH2().replace("${TITLE}", "Find");
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
