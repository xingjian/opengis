package com.gi.server.rest.service.mapservice.map.layer.feature;

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
@Path("/MapService/{serviceName}/{layerId}/{featureId}")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class Resource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") int layerId,
			@PathParam("featureId") int featureId,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f) {
		return result(serviceName, layerId, featureId, token, f);
	}

	@POST
	public String postResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") int layerId,
			@PathParam("featureId") int featureId,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f) {
		return result(serviceName, layerId, featureId, token, f);
	}

	private String result(String serviceName, int layerId, int featureId,
			String token, String f) {
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

				String[] featureIds = new String[] { String.valueOf(featureId) };

				FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection = null;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					Layer layer = instance.getMap().getLayer(layerId);
					featureCollection = layer.query(featureIds);
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(featureCollection,
							featureId);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(serviceName, layerId,
							featureCollection, token, featureId);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(
			FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection,
			int featureId) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONObject objTemp2 = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (featureCollection == null && featureId >= 0) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Feature got is NULL.");
			} else {
				GeometryType geometryType = featureCollection.getSchema()
						.getGeometryDescriptor().getType();
				obj.put("geometryType", EsriJsonUtil
						.geometryType2String(geometryType.getBinding()));
				String geometryField = featureCollection.getSchema()
						.getGeometryDescriptor().getLocalName();

				ArrayList<String> fields = new ArrayList<String>();
				Collection<PropertyDescriptor> propertyDescriptors = featureCollection
						.getSchema().getDescriptors();
				for (Iterator<PropertyDescriptor> itr = propertyDescriptors
						.iterator(); itr.hasNext();) {
					PropertyDescriptor propertyDescriptor = itr.next();
					String name = propertyDescriptor.getName().getLocalPart();
					if (!geometryField.equals(name)) {
						fields.add(name);
					}
				}

				FeatureIterator<? extends Feature> featureIterator = featureCollection
						.features();
				try {
					arrayTemp = new JSONArray();
					while (featureIterator.hasNext()) {
						Feature feature = featureIterator.next();
						objTemp = new JSONObject();

						Geometry geo = (Geometry) feature
								.getDefaultGeometryProperty().getValue();
						objTemp
								.put("geometry", EsriJsonUtil
										.geometry2JSON(geo));

						Iterator<String> iField = fields.iterator();
						objTemp2 = new JSONObject();
						while (iField.hasNext()) {
							String field = iField.next();
							Object attribute = feature.getProperty(field)
									.getValue();
							String value = attribute == null ? "" : attribute
									.toString();
							objTemp2.put(field, value);
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

	private String generateHTMLResult(
			String serviceName,
			int layerId,
			FeatureCollection<? extends FeatureType, ? extends Feature> featureCollection,
			String token, int featureId) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form action='" + contextRoot
					+ "/rest/service/MapService/" + serviceName + "/" + layerId
					+ "/" + featureId + "'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' value='Get Feature (GET)' /><input type='submit' onclick='this.form.method = 'post';' value='Get Feature (POST)' /></td></tr>");
			sb.append("</table></form>");

			if (featureId >= 0) {
				if (featureCollection == null || featureCollection.size() == 0) {
					sb
							.append("<div style='color:#ffaaaa'>Feature not found!<br/></div>");
				} else {
					FeatureIterator<? extends Feature> featureIterator = featureCollection
							.features();
					try {
						String geometryField = featureCollection.getSchema()
								.getGeometryDescriptor().getLocalName();

						if (featureIterator.hasNext()) {
							Feature feature = featureIterator.next();
							sb.append("<div>");

							Geometry geo = (Geometry) feature
									.getDefaultGeometryProperty().getValue();
							if (geo != null) {
								String json = EsriJsonUtil.geometry2JSON(geo)
										.toString();
								sb.append("<br/>Geometry: ");
								sb.append(json);
							}

							String attributes = "";
							for (Iterator<Property> itr = feature
									.getProperties().iterator(); itr.hasNext();) {
								Property property = itr.next();
								String name = property.getDescriptor()
										.getName().getLocalPart();
								if (!geometryField.equals(name)) {
									Object value = property.getValue();
									value = value == null ? "" : value;
									attributes += "<br/>" + name + ": "
											+ value.toString();
								}
							}
							sb.append(attributes);

							sb.append("<br/></div><tr><td><hr/></td></tr>");
						}
					} finally {
						featureIterator.close();
					}
				}
			}

			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Feature (" + serviceName + ")");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; <a href='" + contextRoot
							+ "/rest/service/MapService/" + serviceName + "/"
							+ layerId + "'>" + layerId + "</a> &gt; Feature");
			html.setHeader("Feature ID: " + featureId);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
