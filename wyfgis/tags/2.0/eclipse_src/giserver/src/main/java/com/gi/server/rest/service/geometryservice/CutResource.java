package com.gi.server.rest.service.geometryservice;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.geometry.GeometryToolkit;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;

/**
 * @author wuyf
 * 
 */
@Path("/GeometryService/cut")
public class CutResource {

	@Context
	ServletContext context;

	@GET
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("target") String target,
			@QueryParam("cutter") String cutter) {
		return result(token, f, target, cutter);
	}

	@POST
	@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("target") String target,
			@FormParam("cutter") String cutter) {
		return result(token, f, target, cutter);
	}

	private String result(String token, String f, String target, String cutter) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && (target == null || cutter == null))) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			List<Geometry> resultGeometries = null;
			if (target != null && !"".equals(target) && cutter != null
					&& !"".equals(cutter)) {
				// Handle geometries
				List<Geometry> targetList = EsriJsonUtil
						.json2Geometries(target);
				Geometry cutterGeometry = EsriJsonUtil.json2Geometry(cutter);
				if (cutterGeometry.getGeometryType().equals(
						LineString.class.getSimpleName())) {
					LineString cutterLine = (LineString) cutterGeometry;

					resultGeometries = new ArrayList<Geometry>();
					for (Iterator<Geometry> itr = targetList.iterator(); itr
							.hasNext();) {
						Geometry geo = itr.next();

						if (geo != null) {
							List<Geometry> geos = GeometryToolkit.cut(geo,
									cutterLine);
							for (Iterator<Geometry> itrResult = geos.iterator(); itrResult
									.hasNext();) {
								Geometry g = itrResult.next();
								resultGeometries.add(g);
							}
						}
					}
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(resultGeometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(resultGeometries, token,
						target, cutter);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<Geometry> resultGeometries) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (resultGeometries == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				arrayTemp = new JSONArray();
				for (Iterator<Geometry> itr = resultGeometries.iterator(); itr
						.hasNext();) {
					Geometry geo = itr.next();
					arrayTemp.put(EsriJsonUtil.geometry2JSON(geo));
				}
				obj.put("geometries", arrayTemp);
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(List<Geometry> resultGeometries,
			String token, String target, String cutter) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Target Geometries:</td><td><textarea name='target' rows='10' cols='50'>${TARGET}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>Cutter:</td><td><textarea name='cutter' rows='10' cols='50'>${CUTTER}</textarea></td></tr>");

			sb
					.append("<tr><td colspan='2'><input type='submit' value='Cut' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${TARGET}", target == null ? ""
					: target);
			restBody = restBody.replace("${CUTTER}", cutter == null ? ""
					: cutter);

			if (!(target == null || cutter == null)) {
				StringBuilder sbResult = new StringBuilder();
				int resultCount = resultGeometries.size();
				if (resultCount > 0) {
					sbResult
							.append("<div style='color:#aaffaa'>Results number : "
									+ resultCount + "<br/></div><br/>");
					for (Iterator<Geometry> itr = resultGeometries.iterator(); itr
							.hasNext();) {
						Geometry geo = itr.next();
						sbResult.append("<div>");
						sbResult.append(EsriJsonUtil.geometry2JSON(geo)
								.toString());
						sbResult.append("<br/></div><tr><td><hr/></td></tr>");
					}
				} else {
					sbResult
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				}
				restBody += sbResult.toString();
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("Cut");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Cut");
			html.setHeader("Cut");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
