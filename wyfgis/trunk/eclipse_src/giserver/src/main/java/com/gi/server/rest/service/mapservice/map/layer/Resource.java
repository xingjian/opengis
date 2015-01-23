package com.gi.server.rest.service.mapservice.map.layer;

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

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapLayer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.engine.spatialreference.SpatialReferenceManager;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author wuyf
 * 
 */
@Path("/MapService/{serviceName}/{layerId}")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class Resource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") int layerId,
			@QueryParam("f") @DefaultValue("html") String f) {
		return result(serviceName, layerId, f);
	}

	@POST
	public String postResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") int layerId,
			@FormParam("f") @DefaultValue("html") String f) {
		return result(serviceName, layerId, f);
	}

	private String result(String serviceName, int layerId, String f) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapLayer mapLayer = null;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					mapLayer = instance.getMap().getLayer(layerId)
							.getMapLayer();
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}
				if (mapLayer != null) {
					// Various out format
					if ("json".equals(f)) {
						result = this.generateJSONResult(layerId, mapLayer);
					} else if ("html".equals(f)) {
						result = this.generateHTMLResult(serviceName, layerId,
								mapLayer, mapService.getMapServiceDesc()
										.isNeedToken());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(int layerId, MapLayer mapLayer) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());

			obj.put("id", layerId);
			obj.put("title", mapLayer.getTitle());
			obj.put("name", mapLayer.getFeatureSource().getSchema().getName()
					.getLocalPart());
			obj.put("type", mapLayer.getClass().getSimpleName());
			obj.put("geometryType", mapLayer.getFeatureSource().getSchema()
					.getGeometryDescriptor().getType().getBinding()
					.getSimpleName());

			try {
				ReferencedEnvelope env = mapLayer.getBounds();
				objTemp = new JSONObject();
				objTemp.put("xmin", env.getMinX());
				objTemp.put("xmax", env.getMaxX());
				objTemp.put("ymin", env.getMinY());
				objTemp.put("ymax", env.getMaxY());
				objSR = new JSONObject();
				CoordinateReferenceSystem crs = env
						.getCoordinateReferenceSystem();
				objSR.put("wkid", SpatialReferenceManager.crsToWkid(crs));
				objTemp.put("spatialReference", objSR);
				obj.put("extent", objTemp);
			} catch (Exception ex) {

			}

			arrayTemp = new JSONArray();
			objTemp = new JSONObject();
			Collection<PropertyDescriptor> propertyDescriptors = mapLayer
					.getFeatureSource().getSchema().getDescriptors();
			for (Iterator<PropertyDescriptor> it = propertyDescriptors
					.iterator(); it.hasNext();) {
				PropertyDescriptor propertyDescriptor = it.next();

				objTemp = new JSONObject();
				objTemp
						.put("name", propertyDescriptor.getName()
								.getLocalPart());
				objTemp.put("type", propertyDescriptor.getType().getBinding()
						.getSimpleName());
				arrayTemp.put(objTemp);
			}
			obj.put("fields", arrayTemp);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String serviceName, int layerId,
			MapLayer mapLayer, boolean needToken) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<b>Name: </b>"
					+ mapLayer.getFeatureSource().getSchema().getName()
							.getLocalPart() + "<br/><br/>");
			sb.append("<b>Type: </b>" + mapLayer.getClass().getSimpleName()
					+ "<br/><br/>");
			sb.append("<b>Geometry Type: </b>"
					+ mapLayer.getFeatureSource().getSchema()
							.getGeometryDescriptor().getType().getBinding()
							.getSimpleName() + "<br/><br/>");
			sb.append("<b>Extent: </b><ul>");
			try {
				ReferencedEnvelope env = mapLayer.getBounds();
				sb.append("xmin: " + env.getMinX() + " <br/>");
				sb.append("xmax: " + env.getMaxX() + " <br/>");
				sb.append("ymin: " + env.getMinY() + " <br/>");
				sb.append("ymax: " + env.getMaxY() + " <br/>");
				CoordinateReferenceSystem crs = env
						.getCoordinateReferenceSystem();
				sb.append("Spatial Reference (WKID): "
						+ SpatialReferenceManager.crsToWkid(crs) + " <br/>");
				sb.append("</ul>");
				sb.append("<b>Fields: </b><ul>");
			} catch (Exception ex) {
			}
			Collection<PropertyDescriptor> propertyDescriptors = mapLayer
					.getFeatureSource().getSchema().getDescriptors();
			for (Iterator<PropertyDescriptor> it = propertyDescriptors
					.iterator(); it.hasNext();) {
				PropertyDescriptor propertyDescriptor = it.next();
				sb.append("<li>"
						+ propertyDescriptor.getName().getLocalPart()
						+ " <i>(Type: "
						+ propertyDescriptor.getType().getBinding()
								.getSimpleName() + ")</i></li>");
			}
			sb.append("</ul>");

			sb.append("<b>Supported Operations: </b>");
			if (needToken) {
				sb.append("<img src='" + contextRoot + "/image/lock.png'>");
			}
			sb.append("&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + serviceName + "/" + layerId
					+ "/query'>Query Layer</a>");
			sb.append("<br/><br/>");

			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Layer (" + serviceName + ")");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/MapService'>Map Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/MapService/"
							+ serviceName + "'>" + serviceName + "</a> &gt; "
							+ layerId);
			html.setHeader("Layer: " + mapLayer.getTitle() + "(" + layerId
					+ ")");
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
