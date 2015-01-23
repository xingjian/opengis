package com.gi.giserver.rest.service.mapservice;

import java.util.ArrayList;
import java.util.Collection;
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

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;

import com.gi.giengine.map.desc.MapDesc;
import com.gi.giengine.map.query.SpatialFilterType;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.geometryservice.Project;
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

@Path("/MapService/{mapName}/{layerId}/query")
public class MapLayerQueryResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName, @PathParam("layerId") String layerId,
			@QueryParam("token") String token, @QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometry") String geometry, @QueryParam("geometryType") String geometryType,
			@QueryParam("inSR") String inSR, @QueryParam("spatialRel") String spatialRel,
			@QueryParam("where") String where, @QueryParam("outFields") String outFields,
			@QueryParam("returnGeometry") @DefaultValue("true") String returnGeometry, @QueryParam("outSR") String outSR) {
		return result(mapName, layerId, token, f, geometry, geometryType, inSR, spatialRel, where, outFields,
				returnGeometry, outSR);
	}

	@POST
	public String postResult(@PathParam("mapName") String mapName, @PathParam("layerId") String layerId,
			@FormParam("token") String token, @FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometry") String geometry, @FormParam("geometryType") String geometryType,
			@FormParam("inSR") String inSR, @FormParam("spatialRel") String spatialRel,
			@FormParam("where") String where, @FormParam("outFields") String outFields,
			@FormParam("returnGeometry") @DefaultValue("true") String returnGeometry, @FormParam("outSR") String outSR) {
		return result(mapName, layerId, token, f, geometry, geometryType, inSR, spatialRel, where, outFields,
				returnGeometry, outSR);
	}

	private synchronized String result(String mapName, String layerId, String token, String f, String geometry,
			String geometryType, String inSR, String spatialRel, String where, String outFields, String returnGeometry,
			String outSR) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					if (!TokenService.verifyToken(token) && !("html".equals(f) && where == null && geometry == null)) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				// Handle layerId
				int nLayerId = Integer.parseInt(layerId);

				// Handle geometry
				Geometry geo = null;
				if (geometry != null && !"".equals(geometry)) {
					geo = EsriJsonGeometryUtil.json2Geometry(geometry);
				}

				// Handle spatialRel
				SpatialFilterType relation;
				if ("esriSpatialRelContains".equals(spatialRel)) {
					relation = SpatialFilterType.CONTAINS;
				} else if ("esriSpatialRelCrosses".equals(spatialRel)) {
					relation = SpatialFilterType.CROSSES;
				} else if ("esriSpatialRelEnvelopeIntersects".equals(spatialRel)) {
					relation = SpatialFilterType.ENVELOPE_INTERSECTS;
				} else if ("esriSpatialRelIndexIntersects".equals(spatialRel)) {
					relation = SpatialFilterType.INDEX_INTERSECTS;
				} else if ("esriSpatialRelOverlaps".equals(spatialRel)) {
					relation = SpatialFilterType.OVERLAPS;
				} else if ("esriSpatialRelTouches".equals(spatialRel)) {
					relation = SpatialFilterType.TOUCHES;
				} else if ("esriSpatialRelWithin".equals(spatialRel)) {
					relation = SpatialFilterType.WITHIN;
				} else {
					relation = SpatialFilterType.INTERSECTS;
				}

				// Handle SRs
				String sr = mapDesc.getWkid();
				if (inSR == null || "".equals(inSR)) {
					inSR = sr;
				}
				if (outSR == null || "".equals(outSR)) {
					outSR = sr;
				}

				// Handle returnGeometry
				boolean isReturnGeometry = true;
				if (returnGeometry != null || !"".equals(returnGeometry)) {
					isReturnGeometry = Boolean.parseBoolean(returnGeometry);
				}

				FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection = null;

				try {
					com.gi.giengine.map.param.QueryParam queryParam = new com.gi.giengine.map.param.QueryParam();
					queryParam.setGeometry(geo);
					queryParam.setInSR(inSR);
					queryParam.setSpatialFilterType(relation);
					queryParam.setWhere(where);
					featureCollection = mapService.query(nLayerId, queryParam);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureCollection, where, geometry, sr, outFields,
							isReturnGeometry, outSR);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(mapName, layerId, featureCollection, token, where, geometry, inSR,
							outSR, outFields);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection,
			String where, String geometry, String sr, String outFields, boolean isReturnGeometry, String outSR) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONObject objTemp2 = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");
			if (featureCollection == null && !(where == null && geometry == null)) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Query result is NULL.");
				obj.put("error", EsriJsonUtil.generateJSONError(ServiceError.MAP_QUERY_LAYER_ERROR, details));
			} else {
				objSR = new JSONObject();
				objSR.put("wkid", outSR);
				obj.put("spatialReference", objSR);
				
				obj.put("geometryType", EsriJsonGeometryUtil.geometryType2String(featureCollection.getSchema()
						.getGeometryDescriptor().getType().getClass()));
				String geometryField = featureCollection.getSchema().getGeometryDescriptor().getLocalName();

				ArrayList<String> fields = new ArrayList<String>();
				Collection<PropertyDescriptor> propertyDescriptors = featureCollection.getSchema().getDescriptors();
				for(Iterator<PropertyDescriptor> itr=propertyDescriptors.iterator(); itr.hasNext();){
					PropertyDescriptor propertyDescriptor = itr.next();
					String name = propertyDescriptor.getName().getLocalPart();
					if(!geometryField.equals(name)){
						fields.add(name);
					}
				}

				FeatureIterator<? extends Feature> featureIterator = featureCollection.features();
				try {
					arrayTemp = new JSONArray();
					while (featureIterator.hasNext()) {
						Feature feature = featureIterator.next();
						objTemp = new JSONObject();

						if (isReturnGeometry) {
							Geometry geo = (Geometry) feature.getDefaultGeometryProperty().getValue();
							if (!outSR.equals(sr)) {
								geo = Project.project(geo, sr, outSR);
							}
							objTemp.put("geometry", EsriJsonGeometryUtil.geometry2JSON(geo));
						}

						if (outFields != null && !"".equals(outFields)) {
							objTemp2 = new JSONObject();
							String field = null;
							String value = null;
							if ("*".equals(outFields)) {
								Iterator<String> iField = fields.iterator();
								while (iField.hasNext()) {
									field = iField.next();
									Object attribute = feature.getProperty(field).getValue();
									value = attribute == null ? "" : attribute.toString();
									objTemp2.put(field, value);
								}
							} else {
								String[] strOutFields = outFields.split(",");
								int outFieldsCount = strOutFields.length;
								for (int i = 0; i < outFieldsCount; i++) {
									field = strOutFields[i];
									Object attribute = feature.getProperty(field).getValue();
									value = attribute == null ? "" : attribute.toString();
									objTemp2.put(field, value);
								}
							}
						}
						objTemp.put("attributes", objTemp2);
						arrayTemp.put(objTemp);
					}
					obj.put("features", arrayTemp);
				} finally {
					featureIterator.close();
				}
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String mapName, String layerId,
			FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection, String token, String where,
			String geometry, String inSR, String outSR, String outFields) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			String restBody = "";
			restBody += "<form><table style='border:1px solid #000000;'>";
			restBody += "<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>";
			restBody += "<tr><td>Where: </td><td><input type='text' name='where' value='${WHERE}' size='50' /></td></tr>";
			restBody += "<tr><td>Filter Geometry: </td></tr>";
			restBody += "<tr><td><textarea name='geometry' rows='5' cols='40' >${GEOMETRY}</textarea></td></tr>";
			restBody += "<tr><td>Input Spatial Reference (WKID):</td><td><input type='text' name='inSR' value=''${IN_SR}'' /></td></tr>";
			restBody += "<tr><td>Spatial Relationship:</td><td><select name='spatialRel'><option value='esriSpatialRelIntersects' >Intersects</option><option value='esriSpatialRelContains' >Contains</option><option value='esriSpatialRelCrosses' >Crosses</option><option value='esriSpatialRelEnvelopeIntersects' >Envelope Intersects</option><option value='esriSpatialRelIndexIntersects' >Index Intersects</option><option value='esriSpatialRelOverlaps' >Overlaps</option><option value='esriSpatialRelTouches' >Touches</option><option value='esriSpatialRelWithin' >Within</option></select></td></tr>";
			restBody += "<tr><td>Return Geometry: </td><td><input type='radio' name='returnGeometry' value='true' checked='true'  /> Yes &nbsp;<input type='radio' name='returnGeometry' value='false'  /> No</td></tr>";
			restBody += "<tr><td>Output Spatial Reference (WKID):</td><td><input type='text' name='outSR' value='${OUT_SR}' /></td></tr>";
			restBody += "<tr><td>Return Fields <i>(Separated by ',')</i>: </td><td><input type='text' name='outFields' size='50' value='${OUT_FIELDS}' /></td></tr>";
			restBody += "<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='json' >JSON</option></select></td></tr>";
			restBody += "<tr><td colspan='2' align='left'><input type='submit' value='Query' /></td></tr>";
			restBody += "</table></form>";

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${WHERE}", where == null ? "" : where.replaceAll("'", "&#39;"));
			restBody = restBody.replace("${GEOMETRY}", geometry == null ? "" : geometry);
			restBody = restBody.replace("${IN_SR}", inSR == null ? "" : inSR);
			restBody = restBody.replace("${OUT_SR}", outSR == null ? "" : outSR);
			restBody = restBody.replace("${OUT_FIELDS}", outFields == null ? "" : outFields);

			if (!(where == null && geometry == null)) {
				if (featureCollection == null || featureCollection.size() == 0) {
					restBody += "<div style='color:#ffaaaa'>No results!<br/></div>";
				} else {
					int resultCount = featureCollection.size();
					restBody += "<div style='color:#aaffaa'>Results number : " + resultCount + "<br/></div><br/>";
					if (resultCount > 10) {
						restBody += "<div style='color:#ffff00'>Only top 10 records will be printed<br/></div>";
					}
					int printCount = 0;
					FeatureIterator<? extends Feature> featureIterator = featureCollection.features();
					try {
						String geometryField = featureCollection.getSchema().getGeometryDescriptor().getLocalName();

						while (featureIterator.hasNext() && printCount < 10) {
							Feature feature = featureIterator.next();
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

							String attributes = "";
							for (Iterator<Property> itr = feature.getProperties().iterator(); itr.hasNext();) {
								Property property = itr.next();
								String name = property.getDescriptor().getName().getLocalPart();
								if (!geometryField.equals(name)) {
									Object value = property.getValue();
									value = value == null ? "" : value;
									attributes += "<br/>" + name + ": " + value.toString();
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
					} finally {
						featureIterator.close();
					}
				}
			}

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}", "Query Layer");
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot + "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/" + mapName + "'>" + mapName
							+ "</a> &gt; <a href='" + contextRoot + "/rest/service/MapService/" + mapName + "/"
							+ layerId + "'>" + layerId + "</a> &gt; Query");
			result += ServiceHTML.getH2().replace("${TITLE}", "Query Layer: " + layerId);
			result += ServiceHTML.getRestBody().replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
