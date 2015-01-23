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
import com.gi.engine.util.VersionUtil;
import com.gi.server.core.config.ConfigManager;
import com.gi.server.core.service.tokenservice.TokenService;
import com.gi.server.rest.html.ServiceHTML;
import com.vividsolutions.jts.geom.Geometry;

/**
 * @author wuyf
 * 
 */
@Path("/GeometryService/difference")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class DifferenceResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries") String geometries,
			@QueryParam("geometry") String geometry) {
		return result(token, f, geometries, geometry);
	}

	@POST
	public String postResult(@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries") String geometries,
			@FormParam("geometry") String geometry) {
		return result(token, f, geometries, geometry);
	}

	private String result(String token, String f, String geometries,
			String geometry) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && (geometries == null || geometry == null))) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			List<Geometry> resultGeometries = null;
			if (geometries != null && !"".equals(geometries)
					&& geometry != null && !"".equals(geometry)) {
				// Handle geometries
				List<Geometry> geometryList = EsriJsonUtil
						.json2Geometries(geometries);
				Geometry geometryObj = EsriJsonUtil.json2Geometry(geometry);

				resultGeometries = new ArrayList<Geometry>();
				for (Iterator<Geometry> itr = geometryList.iterator(); itr
						.hasNext();) {
					Geometry geo = itr.next();
					Geometry resultGeo = geo.difference(geometryObj);
					if (!resultGeo.isEmpty()) {
						resultGeometries.add(resultGeo);
					}
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(resultGeometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(resultGeometries, token,
						geometries, geometry);
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
			String token, String geometries, String geometry) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Input Geometries:</td><td><textarea name='geometries' rows='10' cols='50'>${GEOMETRIES}</textarea></td></tr>");
			sb
					.append("<tr valign='top'><td>Difference Geometry:</td><td><textarea name='geometry' rows='10' cols='50'>${GEOMETRY}</textarea></td></tr>");

			sb
					.append("<tr><td colspan='2'><input type='submit' value='Difference' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${GEOMETRIES}",
					geometries == null ? "" : geometries);
			restBody = restBody.replace("${GEOMETRY}", geometry == null ? ""
					: geometry);

			if (!(geometries == null || geometry == null)) {
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
			html.setTitle("Difference");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Difference");
			html.setHeader("Difference");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
