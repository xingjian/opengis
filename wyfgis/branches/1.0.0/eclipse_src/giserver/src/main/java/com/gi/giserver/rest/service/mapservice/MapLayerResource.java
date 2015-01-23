package com.gi.giserver.rest.service.mapservice;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapLayer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.gi.giengine.sr.SpatialReferenceEngine;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */

@Path("/MapService/{mapName}/{layerId}")
public class MapLayerResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName,
			@PathParam("layerId") String layerId,
			@QueryParam("f") @DefaultValue("html") String f) {
		return result(mapName, layerId, f);
	}

	private synchronized String result(String mapName, String layerId, String f) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

			if (mapService != null) {
				MapLayer mapLayer = mapService.getLayer(Integer
						.parseInt(layerId));
				if (mapLayer != null) {
					// Various out format
					if ("json".equals(f)) {
						result = this.generateJSONResult(layerId, mapLayer);
					} else if ("html".equals(f)) {
						result = this.generateHTMLResult(mapName, layerId,
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

	private String generateJSONResult(String layerId, MapLayer mapLayer) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");

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
				objSR.put("wkid", SpatialReferenceEngine.crsToWkid(crs));
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

	private String generateHTMLResult(String mapName, String layerId,
			MapLayer mapLayer, boolean needToken) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			String restBody = "";
			restBody += "<b>Name: </b>"
					+ mapLayer.getFeatureSource().getSchema().getName()
							.getLocalPart() + "<br/><br/>";
			restBody += "<b>Type: </b>" + mapLayer.getClass().getSimpleName()
					+ "<br/><br/>";
			restBody += "<b>Geometry Type: </b>"
					+ mapLayer.getFeatureSource().getSchema()
							.getGeometryDescriptor().getType().getBinding()
							.getSimpleName() + "<br/><br/>";
			restBody += "<b>Extent: </b><ul>";
			try {
				ReferencedEnvelope env = mapLayer.getBounds();
				restBody += "xmin: " + env.getMinX() + " <br/>";
				restBody += "xmax: " + env.getMaxX() + " <br/>";
				restBody += "ymin: " + env.getMinY() + " <br/>";
				restBody += "ymax: " + env.getMaxY() + " <br/>";
				CoordinateReferenceSystem crs = env
						.getCoordinateReferenceSystem();
				restBody += "Spatial Reference (WKID): "
						+ SpatialReferenceEngine.crsToWkid(crs) + " <br/>";
				restBody += "</ul>";
				restBody += "<b>Fields: </b><ul>";
			} catch (Exception ex) {
			}
			Collection<PropertyDescriptor> propertyDescriptors = mapLayer
					.getFeatureSource().getSchema().getDescriptors();
			for (Iterator<PropertyDescriptor> it = propertyDescriptors
					.iterator(); it.hasNext();) {
				PropertyDescriptor propertyDescriptor = it.next();
				restBody += "<li>"
						+ propertyDescriptor.getName().getLocalPart()
						+ " <i>(Type: "
						+ propertyDescriptor.getType().getBinding()
								.getSimpleName() + ")</i></li>";
			}
			restBody += "</ul>";

			restBody += "<b>Supported Operations: </b>";
			if (needToken) {
				restBody += "<img src='" + contextRoot + "/image/lock.png'>";
			}
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName + "/" + layerId
					+ "/query'>Query Layer</a>";
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName + "/" + layerId
					+ "/insert'>Insert Features</a>";
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName + "/" + layerId
					+ "/delete'>Delete Features</a>";
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName + "/" + layerId
					+ "/modify'>Modify Features</a>";
			restBody += "<br/><br/>";

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}",
					"Layer");
			result += ServiceHTML
					.getNav(contextRoot)
					.replace(
							"${CATALOG}",
							" &gt; <a href='"
									+ contextRoot
									+ "/rest/service/MapService'>Map Service</a> &gt; <a href='"
									+ contextRoot + "/rest/service/MapService/"
									+ mapName + "'>" + mapName + "</a> &gt; "
									+ layerId);
			result += ServiceHTML.getH2().replace("${TITLE}",
					"Layer: " + mapLayer.getTitle() + "(" + layerId + ")");
			result += ServiceHTML.getRestBody()
					.replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
