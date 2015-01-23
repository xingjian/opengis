package com.gi.server.rest.service.mapservice.map;

import java.util.ArrayList;

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
import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.arcgis.EsriUnitsUtil;
import com.gi.engine.carto.LayerInfo;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.server.service.TileInfo;
import com.gi.engine.server.service.TileLodInfo;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Envelope;

/**
 * @author wuyf
 * 
 */
@Path("/MapService/{serviceName}")
public class Resource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(@PathParam("serviceName") String serviceName,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f) {
		return result(serviceName, token, f);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(@PathParam("serviceName") String serviceName,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f) {
		return result(serviceName, token, f);
	}

	private String result(String serviceName, String token, String f) {
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

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(mapService);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(mapService);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(MapService mapService) {
		String result = null;

		try {
			MapDesc mapDesc = mapService.getMapDesc();
			MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
			String mapName = mapDesc.getName();

			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONObject objTemp2 = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			obj.put("name", mapName);

			arrayTemp = new JSONArray();
			for (int i = 0, count = mapDesc.getLayerInfos().size(); i < count; i++) {
				LayerInfo layerInfo = mapDesc.getLayerInfo(i);
				objTemp = new JSONObject();
				objTemp.put("id", i);
				objTemp.put("name", layerInfo.getName());
				objTemp.put("defaultVisibility", layerInfo.isVisible());
				objTemp.put("editable", layerInfo.isEditable());
				arrayTemp.put(objTemp);
			}
			obj.put("layers", arrayTemp);

			objSR = new JSONObject();
			objSR.put("wkid", mapDesc.getWkid());
			obj.put("spatialReference", objSR);

			objTemp = new JSONObject();
			Envelope initialExtent = mapDesc.getInitialExtent();
			objTemp.put("xmin", initialExtent.getMinX());
			objTemp.put("xmax", initialExtent.getMaxX());
			objTemp.put("ymin", initialExtent.getMinY());
			objTemp.put("ymax", initialExtent.getMaxY());
			objTemp.put("spatialReference", objSR);
			obj.put("initialExtent", objTemp);

			objTemp = new JSONObject();

			MapServiceInstance instance = (MapServiceInstance) mapService
					.getMapServicePool().checkoutIdleInstance();
			ReferencedEnvelope fullExtent = instance.getFullExtent();
			mapService.getMapServicePool().checkinIdelInstance(instance);

			objTemp.put("xmin", fullExtent.getMinX());
			objTemp.put("xmax", fullExtent.getMaxX());
			objTemp.put("ymin", fullExtent.getMinY());
			objTemp.put("ymax", fullExtent.getMaxY());
			objTemp.put("spatialReference", objSR);
			obj.put("fullExtent", objTemp);

			obj.put("units", EsriUnitsUtil.getUnits(fullExtent
					.getCoordinateReferenceSystem()));
			obj.put("supportedImageFormatTypes", "png,jpg,bmp");

			if (mapServiceDesc.isUseTile()) {
				objTemp = new JSONObject();
				TileInfo tileInfo = mapServiceDesc.getTileInfo();
				objTemp.put("rows", tileInfo.getHeight());
				objTemp.put("cols", tileInfo.getWidth());
				objTemp.put("format", tileInfo.getFormat());
				objTemp2 = new JSONObject();
				objTemp2.put("x", tileInfo.getOriginX());
				objTemp2.put("y", tileInfo.getOriginY());
				objTemp.put("origin", objTemp2);
				arrayTemp = new JSONArray();
				ArrayList<TileLodInfo> tileLodDescs = tileInfo
						.getTileLodInfos();
				int lodCount = tileLodDescs.size();
				for (int i = 0; i < lodCount; i++) {
					TileLodInfo tileLodDesc = tileLodDescs.get(i);
					objTemp2 = new JSONObject();
					objTemp2.put("level", tileLodDesc.getLevel());
					objTemp2.put("resolution", tileLodDesc.getResolution());
					objTemp2.put("scale", tileLodDesc.getScale());
					arrayTemp.put(objTemp2);
				}
				objTemp.put("lods", arrayTemp);
				obj.put("tileInfo", objTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(MapService mapService) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			MapDesc mapDesc = mapService.getMapDesc();
			MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
			String mapName = mapDesc.getName();
			String serviceName = mapService.getServiceName();

			StringBuilder sb = new StringBuilder();
			sb.append("<b>Layers: </b>");
			sb.append("<ul>");
			int layerCount = mapDesc.getLayerInfos().size();
			for (int i = 0; i < layerCount; i++) {
				LayerInfo layerInfo = mapDesc.getLayerInfo(i);
				sb.append("<li><a href='" + contextRoot
						+ "/rest/service/MapService/" + serviceName + "/" + i
						+ "'>" + layerInfo.getName() + "</a>(" + i + ")</li>");
				sb.append(layerInfo.isVisible() ? "[visible]" : "");
				sb.append(layerInfo.isEditable() ? "[editable]" : "");
			}
			sb.append("</ul>");

			sb.append("<b>SpatialReference: </b>");
			sb.append(mapDesc.getWkid());
			sb.append("<br/><br/>");

			Envelope initialExtent = mapDesc.getInitialExtent();
			sb.append("<b>Initial Extent: </b>");
			sb.append("<br/>xmin: " + initialExtent.getMinX());
			sb.append("<br/>xmax: " + initialExtent.getMaxX());
			sb.append("<br/>ymin: " + initialExtent.getMinY());
			sb.append("<br/>ymax: " + initialExtent.getMaxY());
			sb.append("<br/><br/>");

			ReferencedEnvelope fullExtent = null;
			MapServiceInstance instance = (MapServiceInstance) mapService
					.getMapServicePool().checkoutIdleInstance();
			try {
				fullExtent = instance.getFullExtent();
			} finally {
				mapService.getMapServicePool().checkinIdelInstance(instance);
			}

			double xminFull = fullExtent.getMinX();
			double xmaxFull = fullExtent.getMaxX();
			double yminFull = fullExtent.getMinY();
			double ymaxFull = fullExtent.getMaxY();
			sb.append("<b>Full Extent: </b>");
			sb.append("<br/>xmin: " + xminFull);
			sb.append("<br/>xmax: " + xmaxFull);
			sb.append("<br/>ymin: " + yminFull);
			sb.append("<br/>ymax: " + ymaxFull);
			sb.append("<br/><br/>");

			sb.append("<b>Units: </b>");
			sb.append(EsriUnitsUtil.getUnits(fullExtent
					.getCoordinateReferenceSystem()));
			sb.append("<br/><br/>");

			if (mapServiceDesc.isUseTile()) {
				TileInfo tileInfo = mapServiceDesc.getTileInfo();
				int width = tileInfo.getWidth();
				int height = tileInfo.getHeight();
				double originX = tileInfo.getOriginX();
				double originY = tileInfo.getOriginY();
				sb.append("<b>Tile Info: </b>");
				sb.append("<ul>");
				sb.append("<li><b>Width: </b>" + width + "</li>");
				sb.append("<li><b>Height: </b>" + height + "</li>");
				sb.append("<li><b>Format: </b>" + tileInfo.getFormat()
						+ "</li>");
				sb.append("<li><b>OriginX: </b>" + originX + "</li>");
				sb.append("<li><b>OriginY: </b>" + originY + "</li>");
				ArrayList<TileLodInfo> tileLodDescs = tileInfo
						.getTileLodInfos();
				int lodCount = tileLodDescs.size();
				sb.append("<li><b>Levels: </b> (" + lodCount + ")</li>");
				sb.append("<ul>");
				for (int i = 0; i < lodCount; i++) {
					TileLodInfo tileLodInfo = tileLodDescs.get(i);
					int level = tileLodInfo.getLevel();
					double resolution = tileLodInfo.getResolution();
					int startRow = (int) ((originY - ymaxFull) / height / resolution);
					int startCol = (int) ((xminFull - originX) / width / resolution);
					int endRow = (int) ((originY - yminFull) / height / resolution);
					int endCol = (int) ((xmaxFull - originX) / width / resolution);
					sb.append("<li><b>Level: </b>" + level);
					sb.append(" [ ");
					sb.append("<a href='" + contextRoot
							+ "/rest/service/MapService/" + serviceName
							+ "/tile/" + level + "/" + startRow + "/"
							+ startCol + "'>Start Tile</a>");
					sb.append(" , ");
					sb.append("<a href='" + contextRoot
							+ "/rest/service/MapService/" + serviceName
							+ "/tile/" + level + "/" + endRow + "/" + endCol
							+ "'>End Tile</a>");
					sb.append(" ]");
					sb.append("<ul>");
					sb.append("Resolution: " + resolution + "<br/>");
					sb.append("Scale: " + tileLodInfo.getScale());
					sb.append("</ul>");
					sb.append("</li>");
				}
				sb.append("</ul>");
				sb.append("</ul>");
				sb.append("<br/><br/>");
			}

			sb.append("<b>Supported Image Formats: </b>");
			sb.append("PNG/JPG/GIF/BMP");
			sb.append("<br/><br/>");

			sb.append("<hr/>");

			sb.append("<b>Supported Operations: </b>");
			if (mapServiceDesc.isNeedToken()) {
				sb.append("<img src='" + contextRoot + "/image/lock.png'>");
			}
			sb.append("&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + serviceName
					+ "/export'>Export</a>");
			sb.append("&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + serviceName
					+ "/identify'>Identify</a>");
			sb.append("&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + serviceName
					+ "/find'>Find</a>");
			sb.append("<br/><br/>");

			String restBody = sb.toString();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle(serviceName);
			html.setCatalog(" &gt; <a href='" + contextRoot
					+ "/rest/service/MapService'>Map Service</a> &gt; "
					+ serviceName);
			html.setHeader(mapName);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
