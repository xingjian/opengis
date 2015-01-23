package com.gi.server.rest.service.geometryservice;

import java.util.ArrayList;
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

import org.geotools.graph.util.geom.GeometryUtil;
import org.json.JSONObject;

import com.gi.engine.arcgis.EsriJsonUtil;
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author wuyf
 * 
 */
@Path("/GeometryService/convexHull")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class ConvexHullResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries") String geometries) {
		return result(token, f, geometries);
	}

	@POST
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries") String geometries) {
		return result(token, f, geometries);
	}

	private String result(String token, String f, String geometries) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && geometries == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			Geometry resultGeometry = null;
			if (geometries != null && !"".equals(geometries)) {

				// Handle geometries
				List<Geometry> geometriesList = EsriJsonUtil
						.json2Geometries(geometries);
				Geometry collection = GeometryUtil.gf().buildGeometry(
						geometriesList);
				resultGeometry = collection.convexHull();
			}

			// Various out format
			if ("json".equals(f)) {
				result = this
						.generateJSONResult(resultGeometry, geometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(resultGeometry, token,
						geometries);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(Geometry resultGeometry,
			String geometries) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (geometries != null && resultGeometry == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				obj.put("geometryType", EsriJsonUtil
						.geometryType2String(resultGeometry.getClass()));
				obj.put("geometry", EsriJsonUtil
						.geometry2JSON(resultGeometry));
			}

			result = obj.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateHTMLResult(Geometry resultGeometry,
			String token, String geometries) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Geometries:</td><td><textarea name='geometries' rows='10' cols='50'>${GEOMETRIES}</textarea></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='ConvexHull' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${GEOMETRIES}",
					geometries == null ? "" : geometries);

			if (!(geometries == null)) {
				StringBuilder sbResult = new StringBuilder();
				if (resultGeometry != null) {
					sbResult
							.append("<div style='color:#aaffaa'>Result: <br/></div><br/><div>");
					sbResult.append(EsriJsonUtil.geometry2JSON(
							resultGeometry).toString());
					sbResult.append("<br/></div><tr><td><hr/></td></tr>");
				} else {
					sbResult
							.append("<div style='color:#ffaaaa'>No results!<br/></div>");
				}
				restBody += sbResult.toString();
			}

			String contextRoot = context.getContextPath();

			ServiceHTML html = new ServiceHTML();
			html.setContextRoot(contextRoot);
			html.setTitle("ConvexHull");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; ConvexHull");
			html.setHeader("ConvexHull");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
