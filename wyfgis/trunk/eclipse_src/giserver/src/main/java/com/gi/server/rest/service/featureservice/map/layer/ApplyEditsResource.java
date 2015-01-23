package com.gi.server.rest.service.featureservice.map.layer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.identity.FeatureId;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.carto.Layer;
import com.gi.engine.carto.MapDesc;
import com.gi.engine.server.service.MapServiceDesc;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.service.ServiceManager;
import com.gi.server.core.service.mapservice.MapService;
import com.gi.server.core.service.mapservice.MapServiceInstance;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;

/**
 * @author wuyf
 * 
 */
@Path("/FeatureService/{serviceName}/{layerId}/applyEdits")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class ApplyEditsResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("adds") String adds,
			@QueryParam("updates") String updates,
			@QueryParam("deletes") String deletes) {
		return result(serviceName, layerId, token, f, adds, updates, deletes);
	}

	@POST
	public String postResult(@PathParam("serviceName") String serviceName,
			@PathParam("layerId") String layerId,
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("adds") String adds,
			@FormParam("updates") String updates,
			@FormParam("deletes") String deletes) {
		return result(serviceName, layerId, token, f, adds, updates, deletes);
	}

	private String result(String serviceName, String layerId, String token,
			String f, String adds, String updates, String deletes) {
		String result = null;

		try {
			MapService mapService = ServiceManager.getMapService(serviceName);

			if (mapService != null) {
				MapServiceDesc mapServiceDesc = mapService.getMapServiceDesc();
				if (mapServiceDesc.isNeedToken()) {
					boolean htmlNoParams = "html".equals(f) && adds == null
							&& updates == null && deletes == null;
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

				FeatureType featureType = null;
				MapServiceInstance instance = (MapServiceInstance) mapService
						.getMapServicePool().checkoutIdleInstance();
				try {
					Layer layer = instance.getMap().getLayer(nLayerId);
					featureType = layer.getMapLayer().getFeatureSource()
							.getSchema();
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Handle features
				FeatureCollection<SimpleFeatureType, SimpleFeature> addsFeatureCollection = null;
				if (adds != null) {
					addsFeatureCollection = EsriJsonUtil
							.json2FeatureCollection(adds,
									(SimpleFeatureType) featureType);
				}
				FeatureCollection<SimpleFeatureType, SimpleFeature> updatesFeatureCollection = null;
				if (updates != null) {
					updatesFeatureCollection = EsriJsonUtil
							.json2FeatureCollection(updates,
									(SimpleFeatureType) featureType);
				}
				String[] deleteFeatureIds = null;
				if (deletes != null) {
					deleteFeatureIds = deletes.split(",");
				}

				instance = (MapServiceInstance) mapService.getMapServicePool()
						.checkoutIdleInstance();

				List<FeatureId> addFeatureIds = null;
				HashMap<FeatureId, Boolean> updateFeatureIds = null;
				boolean deleteSuccess = false;
				try {
					Layer layer = instance.getMap().getLayer(nLayerId);

					try {
						addFeatureIds = layer.add(addsFeatureCollection);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						updateFeatureIds = layer
								.update(updatesFeatureCollection);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						deleteSuccess = layer.delete(deleteFeatureIds);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} finally {
					if (instance != null) {
						mapService.getMapServicePool().checkinIdelInstance(
								instance);
					}
				}

				// Various out format
				if ("json".equals(f)) {
					result = this.generateJSONResult(addFeatureIds,
							updateFeatureIds, deleteSuccess, deleteFeatureIds,
							adds, updates, deletes);
				} else if ("html".equals(f)) {
					result = this.generateHTMLResult(serviceName, layerId,
							addFeatureIds, updateFeatureIds, deleteSuccess,
							deleteFeatureIds, token, adds, updates, deletes);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<FeatureId> addFeatureIds,
			HashMap<FeatureId, Boolean> updateFeatureIds,
			boolean deleteSuccess, String[] deleteFeatureIds, String adds,
			String updates, String deletes) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONObject objTemp = null;
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (adds != null) {
				arrayTemp = new JSONArray();
				for (Iterator<FeatureId> itr = addFeatureIds.iterator(); itr
						.hasNext();) {
					FeatureId featureId = itr.next();
					String fid = featureId.getID();
					String id = fid.substring(fid.lastIndexOf(".") + 1);
					int nId = Integer.valueOf(id);
					objTemp = new JSONObject();
					objTemp.put("objectId", nId);
					objTemp.put("success", nId >= 0 ? true : false);
					arrayTemp.put(objTemp);
				}
				obj.put("addResults", arrayTemp);
			}
			if (updates != null) {
				arrayTemp = new JSONArray();
				for (Iterator<Entry<FeatureId, Boolean>> itr = updateFeatureIds
						.entrySet().iterator(); itr.hasNext();) {
					Entry<FeatureId, Boolean> update = itr.next();
					String fid = update.getKey().getID();
					String id = fid.substring(fid.lastIndexOf(".") + 1);
					int nId = Integer.valueOf(id);
					objTemp = new JSONObject();
					objTemp.put("objectId", nId);
					objTemp.put("success", update.getValue());
					arrayTemp.put(objTemp);
				}
				obj.put("updateResults", arrayTemp);
			}
			if (deletes != null) {
				arrayTemp = new JSONArray();
				for (int i = 0, count = deleteFeatureIds.length; i < count; i++) {
					String fid = deleteFeatureIds[i];
					String id = fid.substring(fid.lastIndexOf(".") + 1);
					int nId = Integer.valueOf(id);
					objTemp = new JSONObject();
					objTemp.put("objectId", nId);
					objTemp.put("success", deleteSuccess);
					arrayTemp.put(objTemp);
				}
				obj.put("deleteResults", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(String serviceName, String layerId,
			List<FeatureId> addFeatureIds,
			HashMap<FeatureId, Boolean> updateFeatureIds,
			boolean deleteSuccess, String[] deleteFeatureIds, String token,
			String adds, String updates, String deletes) {
		String result = "";

		try {
			String contextRoot = context.getContextPath();

			StringBuilder sb = new StringBuilder();
			sb.append("<form action='" + contextRoot
					+ "/rest/service/FeatureService/" + serviceName + "/"
					+ layerId + "/applyEdits'><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr><td>Adds:</td><td><textarea name='adds' rows='5' cols='40' >${ADDS}</textarea></td></tr>");
			sb
					.append("<tr><td>Updates:</td><td><textarea name='updates' rows='5' cols='40' >${UPDATES}</textarea></td></tr>");
			sb
					.append("<tr><td>Deletes:</td><td><input size='50' type='text' name='deletes' value='${DELETES}' /></td></tr>");
			sb
					.append("<tr><td>Format:</td><td><select name='f'><option value='html' >HTML</option><option value='json' >JSON</option></select></td></tr>");
			sb
					.append("<tr><td colspan='2' align='left'><input type='submit' onclick='this.form.method = 'post';' value='Apply Edits (POST)' /></td></tr>");
			sb.append("</table></form>");

			if (adds != null) {
				if (addFeatureIds == null || addFeatureIds.size() == 0) {
					sb.append("<div style='color:#ffaaaa'>No adds!<br/></div>");
				} else {
					int resultCount = addFeatureIds.size();
					sb
							.append("<div style='color:#aaffaa'>Add results number : "
									+ resultCount + "<br/></div><br/>");
					for (Iterator<FeatureId> itr = addFeatureIds.iterator(); itr
							.hasNext();) {
						FeatureId featureId = itr.next();
						String fid = featureId.getID();
						String id = fid.substring(fid.lastIndexOf(".") + 1);
						sb.append("<div>");
						sb.append("<br/>Add Feature ID: ");
						sb
								.append("<a href='" + contextRoot
										+ "/rest/service/FeatureService/"
										+ serviceName + "/" + layerId + "/"
										+ id + "'>" + id + "</a>");
						int nId = Integer.valueOf(id);
						sb.append(nId >= 0 ? "success" : "failed");
						sb.append("<br/></div><tr><td><hr/></td></tr>");
					}
				}
			}
			if (updates != null) {
				if (updateFeatureIds == null || updateFeatureIds.size() == 0) {
					sb
							.append("<div style='color:#ffaaaa'>No updates!<br/></div>");
				} else {
					int resultCount = updateFeatureIds.size();
					sb
							.append("<div style='color:#aaffaa'>Update results number : "
									+ resultCount + "<br/></div><br/>");
					for (Iterator<Entry<FeatureId, Boolean>> itr = updateFeatureIds
							.entrySet().iterator(); itr.hasNext();) {
						Entry<FeatureId, Boolean> update = itr.next();
						String fid = update.getKey().getID();
						String id = fid.substring(fid.lastIndexOf(".") + 1);
						sb.append("<div>");
						sb.append("<br/>Update Feature ID: ");
						sb
								.append("<a href='" + contextRoot
										+ "/rest/service/FeatureService/"
										+ serviceName + "/" + layerId + "/"
										+ id + "'>" + id + "</a>");
						sb.append(update.getValue() ? "success" : "failed");
						sb.append("<br/></div><tr><td><hr/></td></tr>");
					}
				}
			}
			if (deletes != null) {
				if (deleteSuccess == false) {
					sb
							.append("<div style='color:#ffaaaa'>No deletes!<br/></div>");
				} else {
					int resultCount = deleteFeatureIds.length;
					sb
							.append("<div style='color:#aaffaa'>Delete results number : "
									+ resultCount + "<br/></div><br/>");
					for (int i = 0, count = deleteFeatureIds.length; i < count; i++) {
						String fid = deleteFeatureIds[i];
						String id = fid.substring(fid.lastIndexOf(".") + 1);
						sb.append("<div>");
						sb.append("<br/>Update Feature ID: ");
						sb
								.append("<a href='" + contextRoot
										+ "/rest/service/FeatureService/"
										+ serviceName + "/" + layerId + "/"
										+ id + "'>" + id + "</a>");
						sb.append(deleteSuccess ? "success" : "failed");
						sb.append("<br/></div><tr><td><hr/></td></tr>");
					}
				}
			}

			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${ADDS}", adds == null ? "" : adds);
			restBody = restBody.replace("${UPDATES}", updates == null ? ""
					: updates);
			restBody = restBody.replace("${DELETES}", deletes == null ? ""
					: deletes);

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Apply Edits (" + serviceName + ")");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/FeatureService'>Feature Service</a> &gt; <a href='"
							+ contextRoot + "/rest/service/FeatureService/"
							+ serviceName + "'>" + serviceName
							+ "</a> &gt; <a href='" + contextRoot
							+ "/rest/service/FeatureService/" + serviceName
							+ "/" + layerId + "'>" + layerId
							+ "</a> &gt; Apply Edits");
			html.setHeader("Apply Edits Layer ID: " + layerId);
			html.setRestBody(restBody);

			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
