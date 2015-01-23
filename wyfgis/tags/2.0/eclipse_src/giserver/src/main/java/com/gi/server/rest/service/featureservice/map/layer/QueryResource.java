package com.gi.server.rest.service.featureservice.map.layer;

import java.util.ArrayList;
import java.util.Collection;
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

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.PropertyDescriptor;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.carto.Layer;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.carto.SpatialFilterType;
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
@Path("/FeatureService/{serviceName}/{layerId}/query")
public class QueryResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(
			@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("objectIds") String objectIds,
			@QueryParam("geometry") String geometry,
			@QueryParam("geometryType") String geometryType,
			@QueryParam("inSR") String inSR,
			@QueryParam("spatialRel") String spatialRel,
			@QueryParam("where") String where,
			@QueryParam("outFields") String outFields,
			@QueryParam("returnGeometry") @DefaultValue("true") boolean returnGeometry,
			@QueryParam("outSR") String outSR,
			@QueryParam("returnIdsOnly") @DefaultValue("false") boolean returnIdsOnly) {
		return result(serviceName, layerId, token, f, objectIds, geometry,
				geometryType, inSR, spatialRel, where, outFields,
				returnGeometry, outSR, returnIdsOnly);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(
			@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("objectIds") String objectIds,
			@FormParam("geometry") String geometry,
			@FormParam("geometryType") String geometryType,
			@FormParam("inSR") String inSR,
			@FormParam("spatialRel") String spatialRel,
			@FormParam("where") String where,
			@FormParam("outFields") String outFields,
			@FormParam("returnGeometry") @DefaultValue("true") boolean returnGeometry,
			@FormParam("outSR") String outSR,
			@FormParam("returnIdsOnly") @DefaultValue("false") boolean returnIdsOnly) {
		return result(serviceName, layerId, token, f, objectIds, geometry,
				geometryType, inSR, spatialRel, where, outFields,
				returnGeometry, outSR, returnIdsOnly);
	}

	private String result(String serviceName, String layerId, String token,
			String f, String objectIds, String geometry, String geometryType,
			String inSR, String spatialRel, String where, String outFields,
			boolean returnGeometry, String outSR, boolean returnIdsOnly) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapDesc mapDesc = mapService.getMapDesc();
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					boolean htmlNoParams = "html".equals(f)
							&& objectIds == null && geometry == null
							&& geometryType == null && inSR == null
							&& spatialRel == null && where == null
							&& outFields == null && outSR == null;
					if (!TokenService.verifyToken(token) && !htmlNoParams) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				// Handle layerId
				int nLayerId = Integer.parseInt(layerId);

				// Handle SRs
				String sr = mapDesc.getWkid();
				if (inSR == null || "".equals(inSR)) {
					inSR = sr;
				}
				if (outSR == null || "".equals(outSR)) {
					outSR = sr;
				}

				String[] featureIds = null;
				com.gi.engine.carto.QueryParam queryParam = null;

				// Handle objectids
				if (objectIds != null && !"".equals(objectIds)) {
					featureIds = objectIds.split(",");
				} else {
					// Handle geometry
					Geometry geo = null;
					if (geometry != null && !"".equals(geometry)) {
						geo = EsriJsonUtil.json2Geometry(geometry);

						if (!sr.equals(inSR)) {
							geo = GeometryToolkit.project(geo, inSR, sr);
						}
					}

					// Handle spatialRel
					SpatialFilterType relation;
					if ("esriSpatialRelContains".equals(spatialRel)) {
						relation = SpatialFilterType.CONTAINS;
					} else if ("esriSpatialRelCrosses".equals(spatialRel)) {
						relation = SpatialFilterType.CROSSES;
					} else if ("esriSpatialRelEnvelopeIntersects"
							.equals(spatialRel)) {
						relation = SpatialFilterType.ENVELOPE_INTERSECTS;
					} else if ("esriSpatialRelIndexIntersects"
							.equals(spatialRel)) {
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

					queryParam = new com.gi.engine.carto.QueryParam();
					queryParam.setGeometry(geo);
					queryParam.setSpatialFilterType(relation);
					queryParam.setWhere(where);
				}

				FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection = null;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					Layer layer = instance.getMap().getLayer(nLayerId);
					if (featureIds != null) {
						featureCollection = layer.query(featureIds);
					} else {
						featureCollection = layer.query(queryParam);
					}
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureCollection, sr,
							outFields, returnGeometry, outSR, returnIdsOnly);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(serviceName, layerId,
							featureCollection, token, objectIds, where,
							geometry, inSR, outSR, returnGeometry, outFields,
							returnIdsOnly);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(
			FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection,
			String sr, String outFields, boolean returnGeometry, String outSR,
			boolean returnIdsOnly) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONObject objTemp2 = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (featureCollection == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Query result is NULL.");
			} else {
				FeatureIterator<? extends Feature> featureIterator = featureCollection
						.features();

				try {
					if (returnIdsOnly) {
						arrayTemp = new JSONArray();
						while (featureIterator.hasNext()) {
							Feature feature = featureIterator.next();
							String fid = feature.getIdentifier().getID();
							String id = fid.substring(fid.lastIndexOf(".") + 1);
							arrayTemp.put(Integer.valueOf(id));
						}

						obj.put("objectIds", arrayTemp);
					} else {
						objSR = new JSONObject();
						objSR.put("wkid", outSR);
						obj.put("spatialReference", objSR);

						GeometryType geometryType = featureCollection
								.getSchema().getGeometryDescriptor().getType();
						obj
								.put("geometryType", EsriJsonUtil
										.geometryType2String(geometryType
												.getBinding()));
						String geometryField = featureCollection.getSchema()
								.getGeometryDescriptor().getLocalName();

						ArrayList<String> fields = new ArrayList<String>();
						Collection<PropertyDescriptor> propertyDescriptors = featureCollection
								.getSchema().getDescriptors();
						for (Iterator<PropertyDescriptor> itr = propertyDescriptors
								.iterator(); itr.hasNext();) {
							PropertyDescriptor propertyDescriptor = itr.next();
							String name = propertyDescriptor.getName()
									.getLocalPart();
							if (!geometryField.equals(name)) {
								fields.add(name);
							}
						}
						arrayTemp = new JSONArray();
						while (featureIterator.hasNext()) {
							Feature feature = featureIterator.next();
							objTemp = new JSONObject();
							if (returnGeometry) {
								Geometry geo = (Geometry) feature
										.getDefaultGeometryProperty()
										.getValue();
								if (!outSR.equals(sr)) {
									geo = GeometryToolkit.project(geo, sr,
											outSR);
								}
								objTemp.put("geometry", EsriJsonUtil
										.geometry2JSON(geo));
							}

							if (outFields != null && !"".equals(outFields)) {
								objTemp2 = new JSONObject();
								String field = null;
								String value = null;
								if ("*".equals(outFields)) {
									Iterator<String> iField = fields.iterator();
									while (iField.hasNext()) {
										field = iField.next();
										Object attribute = feature.getProperty(
												field).getValue();
										value = attribute == null ? ""
												: attribute.toString();
										objTemp2.put(field, value);
									}
								} else {
									String[] strOutFields = outFields
											.split(",");
									int outFieldsCount = strOutFields.length;
									for (int i = 0; i < outFieldsCount; i++) {
										field = strOutFields[i];
										Object attribute = feature.getProperty(
												field).getValue();
										value = attribute == null ? ""
												: attribute.toString();
										objTemp2.put(field, value);
									}
								}
							}
							objTemp.put("attributes", objTemp2);
							arrayTemp.put(objTemp);
						}
						obj.put("features", arrayTemp);
					}
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

	private String generateHTMLResult(
			String serviceName,
			String layerId,
			FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection,
			String token, String objectIds, String where, String geometry,
			String inSR, String outSR, boolean returnGeometry,
			String outFields, boolean returnIdsOnly) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form action='" + contextRoot
					+ "/rest/service/FeatureService/" + serviceName + "/"
					+ layerId + "/query'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Object Ids: </td><td><input type='text' name='objectIds' value='${OBJECT_IDS}' size='50' /></td></tr>");
			sb
					.append("<tr><td>Where: </td><td><input type='text' name='where' value='${WHERE}' size='50' /></td></tr>");
			sb
					.append("<tr><td>Input Geometry:</td><td><textarea name='geometry' rows='5' cols='40' >${GEOMETRY}</textarea></td></tr>");
			sb
					.append("<tr><td>Input Spatial Reference (WKID):</td><td><input type='text' name='inSR' value=''${IN_SR}'' /></td></tr>");
			sb
					.append("<tr><td>Spatial Relationship:</td><td><select name='spatialRel'><option value='esriSpatialRelIntersects' >Intersects</option><option value='esriSpatialRelContains' >Contains</option><option value='esriSpatialRelCrosses' >Crosses</option><option value='esriSpatialRelEnvelopeIntersects' >Envelope Intersects</option><option value='esriSpatialRelIndexIntersects' >Index Intersects</option><option value='esriSpatialRelOverlaps' >Overlaps</option><option value='esriSpatialRelTouches' >Touches</option><option value='esriSpatialRelWithin' >Within</option></select></td></tr>");
			sb
					.append("<tr><td>Return Ids Only: </td><td><input type='radio' name='returnIdsOnly' value='true' "
							+ (returnIdsOnly ? "checked='true'" : "")
							+ " /> Yes &nbsp;<input type='radio' name='returnIdsOnly' value='false' "
							+ (!returnIdsOnly ? "checked='true'" : "")
							+ " /> No</td></tr>");
			sb
					.append("<tr><td>Return Geometry: </td><td><input type='radio' name='returnGeometry' value='true' "
							+ (returnGeometry ? "checked='true'" : "")
							+ "  /> Yes &nbsp;<input type='radio' name='returnGeometry' value='false' "
							+ (!returnGeometry ? "checked='true'" : "")
							+ " /> No</td></tr>");
			sb
					.append("<tr><td>Output Spatial Reference (WKID):</td><td><input type='text' name='outSR' value='${OUT_SR}' /></td></tr>");
			sb
					.append("<tr><td>Return Fields <i>(Separated by ',')</i>: </td><td><input type='text' name='outFields' size='50' value='${OUT_FIELDS}' /></td></tr>");
			sb
					.append("<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='json' >JSON</option></select></td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' value='Query (GET)' /><input type='submit' onclick='this.form.method = 'post';' value='Query (POST)' /></td></tr>");
			sb.append("</table></form>");

			if (objectIds != null || where != null || geometry != null) {
				if (featureCollection == null || featureCollection.size() == 0) {
					sb
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				} else {
					int resultCount = featureCollection.size();
					sb.append("<div style='color:#aaffaa'>Results number : "
							+ resultCount + "<br/></div><br/>");
					if (resultCount > 10) {
						sb
								.append("<div style='color:#ffff00'>Only top 10 records will be printed<br/></div>");
					}
					int printCount = 0;
					FeatureIterator<? extends Feature> featureIterator = featureCollection
							.features();
					try {
						String geometryField = featureCollection.getSchema()
								.getGeometryDescriptor().getLocalName();

						while (featureIterator.hasNext() && printCount < 10) {
							Feature feature = featureIterator.next();
							sb.append("<div>");

							String fid = feature.getIdentifier().getID();
							String featureId = fid.substring(fid
									.lastIndexOf(".") + 1);
							sb.append("<br/>Feature ID: ");
							sb.append("<a href='" + contextRoot
									+ "/rest/service/FeatureService/"
									+ serviceName + "/" + layerId + "/"
									+ featureId + "'>" + featureId + "</a>");

							if (!returnIdsOnly) {
								sb.append("<br/>");

								if (returnGeometry) {
									Geometry geo = (Geometry) feature
											.getDefaultGeometryProperty()
											.getValue();
									if (geo != null) {
										String json = EsriJsonUtil
												.geometry2JSON(geo).toString();
										sb.append("<br/>Geometry: ");
										if (json.length() > 100) {
											sb.append(json.substring(0, 99)
													+ "...");
										} else {
											sb.append(json);
										}
									}
								}

								String attributes = "";
								for (Iterator<Property> itr = feature
										.getProperties().iterator(); itr
										.hasNext();) {
									Property property = itr.next();
									String name = property.getDescriptor()
											.getName().getLocalPart();
									if (!geometryField.equals(name)) {
										Object value = property.getValue();
										value = value == null ? "" : value;
										attributes += "<br/>" + name + ": "
												+ value.toString();
										if (attributes.length() > 100) {
											attributes += "<br/>...";
											break;
										}
									}
								}
								sb.append(attributes);
							}

							sb.append("<br/></div><tr><td><hr/></td></tr>");
							printCount++;
						}
					} finally {
						featureIterator.close();
					}
				}
			}

			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${OBJECT_IDS}", objectIds == null ? ""
					: objectIds);
			restBody = restBody.replace("${WHERE}", where == null ? "" : where
					.replaceAll("'", "&#39;"));
			restBody = restBody.replace("${GEOMETRY}", geometry == null ? ""
					: geometry);
			restBody = restBody.replace("${IN_SR}", inSR == null ? "" : inSR);
			restBody = restBody
					.replace("${OUT_SR}", outSR == null ? "" : outSR);
			restBody = restBody.replace("${OUT_FIELDS}", outFields == null ? ""
					: outFields);

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Query (" + serviceName + ")");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/FeatureService'>Feature Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/FeatureService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; <a href='" + contextRoot
							+ "/rest/service/FeatureService/" + serviceName
							+ "/" + layerId + "'>" + layerId
							+ "</a> &gt; Query");
			html.setHeader("Query Layer ID: " + layerId);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
