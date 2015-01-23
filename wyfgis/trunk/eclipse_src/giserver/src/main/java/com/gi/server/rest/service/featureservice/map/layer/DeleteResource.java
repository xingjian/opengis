package com.gi.server.rest.service.featureservice.map.layer;

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

import org.json.JSONObject;

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
@Path("/FeatureService/{serviceName}/{layerId}/deleteFeatures")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class DeleteResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("objectIds") String objectIds,
			@QueryParam("geometry") String geometry,
			@QueryParam("geometryType") String geometryType,
			@QueryParam("inSR") String inSR,
			@QueryParam("spatialRel") String spatialRel,
			@QueryParam("where") String where) {
		return result(serviceName, layerId, token, f, objectIds, geometry,
				geometryType, inSR, spatialRel, where);
	}

	@POST
	public String postResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("objectIds") String objectIds,
			@FormParam("geometry") String geometry,
			@FormParam("geometryType") String geometryType,
			@FormParam("inSR") String inSR,
			@FormParam("spatialRel") String spatialRel,
			@FormParam("where") String where) {
		return result(serviceName, layerId, token, f, objectIds, geometry,
				geometryType, inSR, spatialRel, where);
	}

	private String result(String serviceName, String layerId, String token,
			String f, String objectIds, String geometry, String geometryType,
			String inSR, String spatialRel, String where) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					boolean htmlNoParams = "html".equals(f)
							&& objectIds == null && geometry == null
							&& geometryType == null && inSR == null
							&& spatialRel == null && where == null;
					if (!TokenService.verifyToken(token) && !htmlNoParams) {
						return TokenService.TOKEN_INVALID_TIP;
					}
				}

				// Handle layerId
				int nLayerId = Integer.parseInt(layerId);

				MapDesc mapDesc = mapService.getMapDesc();
				boolean editable = mapDesc.getLayerInfo(nLayerId).isEditable();
				if (!editable) {
					return null;
				}

				// Handle SRs
				String sr = mapDesc.getWkid();
				if (inSR == null || "".equals(inSR)) {
					inSR = sr;
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

				boolean success = false;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					Layer layer = instance.getMap().getLayer(nLayerId);
					if (featureIds != null) {
						success = layer.delete(featureIds);
					} else {
						success = layer.delete(queryParam);
					}
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(success);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(serviceName, layerId,
							success, token, objectIds, where, geometry, inSR);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(boolean success) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			obj.put("success", success);

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String serviceName, String layerId,
			boolean success, String token, String objectIds, String where,
			String geometry, String inSR) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form action='" + contextRoot
					+ "/rest/service/FeatureService/" + serviceName + "/"
					+ layerId + "/deleteFeatures'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Object Ids: </td><td><input type='text' name='objectIds' value='${OBJECT_IDS}' size='50' /></td></tr>");
			sb
					.append("<tr><td>Where: </td><td><input type='text' name='where' value='${WHERE}' size='50' /></td></tr>");
			sb
					.append("<tr><td>Input Geometry: </td><td><textarea name='geometry' rows='5' cols='40' >${GEOMETRY}</textarea></td></tr>");
			sb
					.append("<tr><td>Input Spatial Reference (WKID):</td><td><input type='text' name='inSR' value=''${IN_SR}'' /></td></tr>");
			sb
					.append("<tr><td>Spatial Relationship:</td><td><select name='spatialRel'><option value='esriSpatialRelIntersects' >Intersects</option><option value='esriSpatialRelContains' >Contains</option><option value='esriSpatialRelCrosses' >Crosses</option><option value='esriSpatialRelEnvelopeIntersects' >Envelope Intersects</option><option value='esriSpatialRelIndexIntersects' >Index Intersects</option><option value='esriSpatialRelOverlaps' >Overlaps</option><option value='esriSpatialRelTouches' >Touches</option><option value='esriSpatialRelWithin' >Within</option></select></td></tr>");
			sb
					.append("<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='json' >JSON</option></select></td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' onclick='this.form.method = 'post';' value='Delete Features (POST)' /></td></tr>");
			sb.append("</table></form>");

			if (objectIds != null || where != null || geometry != null) {
				if (!success) {
					sb
							.append("<div style='color:#ffaaaa'>Delete failed!<br/></div>");
				} else {
					sb.append("<div style='color:#aaffaa'>Delete success!<br/></div><br/>");
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

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Delete Features (" + serviceName + ")");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/FeatureService'>Feature Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/FeatureService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; <a href='" + contextRoot
							+ "/rest/service/FeatureService/" + serviceName
							+ "/" + layerId + "'>" + layerId
							+ "</a> &gt; Delete Features");
			html.setHeader("Delete Features Layer ID: " + layerId);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
