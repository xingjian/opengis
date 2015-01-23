package com.gi.giserver.rest.service.mapservice;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.giengine.map.desc.ExtentDesc;
import com.gi.giengine.map.desc.LayerDesc;
import com.gi.giengine.map.desc.MapDesc;
import com.gi.giserver.core.service.ServiceManager;
import com.gi.giserver.core.service.mapservice.MapService;
import com.gi.giserver.core.service.mapservice.MapServicePool;
import com.gi.giserver.core.service.mapservice.desc.MapServiceDesc;
import com.gi.giserver.core.service.mapservice.desc.TileDesc;
import com.gi.giserver.core.service.mapservice.desc.TileLodDesc;
import com.gi.giserver.core.service.tokenservice.TokenService;
import com.gi.giserver.rest.html.ServiceHTML;

/**
 * @author Wu Yongfeng
 * 
 */
@Path("/MapService/{mapName}")
public class MapResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("mapName") String mapName,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f) {
		return result(mapName, token, f);
	}

	@POST
	public String postResult(@PathParam("mapName") String mapName,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f) {
		return result(mapName, token, f);
	}

	private synchronized String result(String mapName, String token, String f) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(mapName);

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
			String mapName = mapDesc.getMapName();

			JSONObject obj = new JSONObject();
			JSONObject objSR = null;
			JSONObject objTemp = null;
			JSONObject objTemp2 = null;
			JSONArray arrayTemp = null;

			obj.put("about", "Powered by GIServer");

			arrayTemp = new JSONArray();
			int layerCount = mapDesc.getLayerDescs().size();
			for (int i = 0; i < layerCount; i++) {
				LayerDesc layerDesc = mapDesc.getLayerDesc(i);
				objTemp = new JSONObject();
				objTemp.put("id", i);
				objTemp.put("defaultVisibility", layerDesc.isVisible());
				objTemp.put("editable", layerDesc.isEditable());
				arrayTemp.put(objTemp);
			}
			obj.put("layers", arrayTemp);

			objSR = new JSONObject();
			objSR.put("wkid", mapDesc.getWkid());
			obj.put("spatialReference", objSR);

			objTemp = new JSONObject();
			ExtentDesc initialExtentDesc = mapDesc.getInitialExtentDesc();
			objTemp.put("xmin", initialExtentDesc.getXmin());
			objTemp.put("xmax", initialExtentDesc.getXmax());
			objTemp.put("ymin", initialExtentDesc.getYmin());
			objTemp.put("ymax", initialExtentDesc.getYmax());
			objTemp.put("spatialReference", objSR);
			obj.put("initialExtent", objTemp);

			objTemp = new JSONObject();
			ReferencedEnvelope fullExtent = mapService.getMapServicePool()
					.getIdleInstance().getFullExtent();
			objTemp.put("xmin", fullExtent.getMinX());
			objTemp.put("xmax", fullExtent.getMaxX());
			objTemp.put("ymin", fullExtent.getMinY());
			objTemp.put("ymax", fullExtent.getMaxY());
			objTemp.put("spatialReference", objSR);
			obj.put("fullExtent", objTemp);

			obj.put("supportedImageFormatTypes", "png,jpg,bmp");

			if (mapServiceDesc.isUseTile()) {
				objTemp = new JSONObject();
				TileDesc tileDesc = mapServiceDesc.getTileDesc();
				objTemp.put("rows", tileDesc.getHeight());
				objTemp.put("cols", tileDesc.getWidth());
				objTemp.put("format", tileDesc.getFormat());
				objTemp2 = new JSONObject();
				objTemp2.put("x", tileDesc.getOriginX());
				objTemp2.put("y", tileDesc.getOriginY());
				objTemp.put("origin", objTemp2);
				arrayTemp = new JSONArray();
				ArrayList<TileLodDesc> tileLodDescs = tileDesc
						.getTileLodDescs();
				int lodCount = tileLodDescs.size();
				for (int i = 0; i < lodCount; i++) {
					TileLodDesc tileLodDesc = tileLodDescs.get(i);
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
			String mapName = mapDesc.getMapName();

			String restBody = "";

			restBody += "<b>Layers: </b>";
			restBody += "<ul>";
			int layerCount = mapDesc.getLayerDescs().size();
			for (int i = 0; i < layerCount; i++) {
				LayerDesc layerDesc = mapDesc.getLayerDesc(i);
				restBody += "<li><a href='" + contextRoot
						+ "/rest/service/MapService/" + mapName + "/" + i
						+ "'>" + layerDesc.getName() + "</a>(" + i + ")</li>";
				restBody += layerDesc.isVisible() ? "[visible]" : "";
				restBody += layerDesc.isEditable() ? "[editable]" : "";
			}
			restBody += "</ul>";

			restBody += "<b>SpatialReference: </b>";
			restBody += mapDesc.getWkid();
			restBody += "<br/><br/>";

			ExtentDesc initialExtent = mapDesc.getInitialExtentDesc();
			restBody += "<b>Initial Extent: </b>";
			restBody += "<br/>xmin: " + initialExtent.getXmin();
			restBody += "<br/>xmax: " + initialExtent.getXmax();
			restBody += "<br/>ymin: " + initialExtent.getYmin();
			restBody += "<br/>ymax: " + initialExtent.getYmax();
			restBody += "<br/><br/>";

			ReferencedEnvelope fullExtent = mapService.getMapServicePool()
					.getIdleInstance().getFullExtent();
			double xminFull = fullExtent.getMinX();
			double xmaxFull = fullExtent.getMaxX();
			double yminFull = fullExtent.getMinY();
			double ymaxFull = fullExtent.getMaxY();
			restBody += "<b>Full Extent: </b>";
			restBody += "<br/>xmin: " + xminFull;
			restBody += "<br/>xmax: " + xmaxFull;
			restBody += "<br/>ymin: " + yminFull;
			restBody += "<br/>ymax: " + ymaxFull;
			restBody += "<br/><br/>";

			if (mapServiceDesc.isUseTile()) {
				TileDesc tileDesc = mapServiceDesc.getTileDesc();
				int width = tileDesc.getWidth();
				int height = tileDesc.getHeight();
				double originX = tileDesc.getOriginX();
				double originY = tileDesc.getOriginY();
				restBody += "<b>Tile Info: </b>";
				restBody += "<ul>";
				restBody += "<li><b>Width: </b>" + width + "</li>";
				restBody += "<li><b>Height: </b>" + height + "</li>";
				restBody += "<li><b>Format: </b>" + tileDesc.getFormat()
						+ "</li>";
				restBody += "<li><b>OriginX: </b>" + originX + "</li>";
				restBody += "<li><b>OriginY: </b>" + originY + "</li>";
				ArrayList<TileLodDesc> tileLodDescs = tileDesc
						.getTileLodDescs();
				int lodCount = tileLodDescs.size();
				restBody += "<li><b>Levels: </b> (" + lodCount + ")</li>";
				restBody += "<ul>";
				for (int i = 0; i < lodCount; i++) {
					TileLodDesc tileLodDesc = tileLodDescs.get(i);
					int level = tileLodDesc.getLevel();
					double resolution = tileLodDesc.getResolution();
					int startRow = (int) ((originY - ymaxFull) / height / resolution);
					int startCol = (int) ((xminFull - originX) / width / resolution);
					int endRow = (int) ((originY - yminFull) / height / resolution);
					int endCol = (int) ((xmaxFull - originX) / width / resolution);
					restBody += "<li><b>Level: </b>" + level;
					restBody += " [ ";
					restBody += "<a href='" + contextRoot
							+ "/rest/service/MapService/" + mapName + "/tile/"
							+ level + "/" + startRow + "/" + startCol
							+ "'>Start Tile</a>";
					restBody += " , ";
					restBody += "<a href='" + contextRoot
							+ "/rest/service/MapService/" + mapName + "/tile/"
							+ level + "/" + endRow + "/" + endCol
							+ "'>End Tile</a>";
					restBody += " ]";
					restBody += "<ul>";
					restBody += "Resolution: " + resolution + "<br/>";
					restBody += "Scale: " + tileLodDesc.getScale();
					restBody += "</ul>";
					restBody += "</li>";
				}
				restBody += "</ul>";
				restBody += "</ul>";
				restBody += "<br/><br/>";
			}

			restBody += "<b>Supported Image Formats: </b>";
			restBody += "PNG/JPG/GIF/BMP";
			restBody += "<br/><br/>";

			restBody += "<hr/>";

			restBody += "<b>Supported Operations: </b>";
			if (mapServiceDesc.isNeedToken()) {
				restBody += "<img src='" + contextRoot + "/image/lock.png'>";
			}
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName
					+ "/export'>Export</a>";
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName
					+ "/identify'>Identify</a>";
			restBody += "&nbsp;&nbsp;<a href='" + contextRoot
					+ "/rest/service/MapService/" + mapName + "/find'>Find</a>";
			restBody += "<br/><br/>";

			result += ServiceHTML.getHeader(contextRoot).replace("${TITLE}",
					mapName);
			result += ServiceHTML.getNav(contextRoot).replace(
					"${CATALOG}",
					" &gt; <a href='" + contextRoot
							+ "/rest/service/MapService'>Map Service</a> &gt; "
							+ mapName);
			result += ServiceHTML.getH2().replace("${TITLE}", mapName);
			result += ServiceHTML.getRestBody()
					.replace("${RESTBODY}", restBody);
			result += ServiceHTML.getFooter();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
