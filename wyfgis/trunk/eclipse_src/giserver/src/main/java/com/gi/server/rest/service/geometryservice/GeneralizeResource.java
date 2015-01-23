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


/**
 * @author wuyf
 *
 */
@Path("/GeometryService/generalize")
@Produces( { MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
public class GeneralizeResource {

	@Context
	ServletContext context;

	@GET
	public String getResult(
			@QueryParam("token") String token,
			@QueryParam("f") @DefaultValue("html") String f,
			@QueryParam("geometries") String geometries,
			@QueryParam("maxDeviation") double maxDeviation) {
		return result(token, f, geometries,maxDeviation);
	}

	@POST
	public String postResult(
			@FormParam("token") String token,
			@FormParam("f") @DefaultValue("html") String f,
			@FormParam("geometries") String geometries,
			@FormParam("maxDeviation") double maxDeviation) {
		return result(token, f, geometries,maxDeviation);
	}

	private String result(String token, String f, String geometries,double maxDeviation) {
		String result = null;

		try {
			if (ConfigManager.getServerConfig()
					.isInternalServiceNeedTokenVerify()) {
				if (!TokenService.verifyToken(token)
						&& !("html".equals(f) && geometries == null)) {
					return TokenService.TOKEN_INVALID_TIP;
				}
			}

			List<Geometry> resultGeometries = null;
			if (geometries != null && !"".equals(geometries) && maxDeviation>0) {

				// Handle geometries
				List<Geometry> geometriesList = EsriJsonUtil
						.json2Geometries(geometries);


				// Do generalize
				resultGeometries = new ArrayList<Geometry>();
				for (Iterator<Geometry> itr = geometriesList.iterator(); itr
						.hasNext();) {
					Geometry geo = itr.next();
					geo = GeometryToolkit.generalize(geo, maxDeviation);
					resultGeometries.add(geo);
				}
			}

			// Various out format
			if ("json".equals(f)) {
				result = this.generateJSONResult(resultGeometries, geometries);
			} else if ("html".equals(f)) {
				result = this.generateHTMLResult(resultGeometries, token,
						geometries, maxDeviation);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String generateJSONResult(List<Geometry> resultGeometries,
			String geometries) {
		String result = null;

		try {
			JSONObject obj = new JSONObject();
			JSONArray arrayTemp = null;

			obj.put("GIServerVersion", VersionUtil.getCurrentversion());
			if (geometries != null && resultGeometries == null) {
				ArrayList<String> details = new ArrayList<String>();
				details.add("Result is NULL.");
			} else {
				Geometry geo = null;
				arrayTemp = new JSONArray();
				Iterator<Geometry> it = resultGeometries.iterator();
				while (it.hasNext()) {
					geo = it.next();
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
			String token, String geometries, double maxDeviation) {
		String result = "";

		try {
			StringBuilder sb = new StringBuilder();
			sb.append("<form><table class='formTable'>");
			sb
					.append("<tr><td>Token:</td><td><input size='50' type='text' name='token' value='${TOKEN}' /></td></tr>");
			sb
					.append("<tr valign='top'><td>Geometries:</td><td><textarea name='geometries' rows='10' cols='50'>${GEOMETRIES}</textarea></td></tr>");
			sb
					.append("<tr><td>Maximum Segment Length:</td><td><input type='text' name='maxDeviation' value='${MAX_DEVIATION}' /></td></tr>");
			sb
					.append("<tr><td colspan='2'><input type='submit' value='Generalize' /></td></tr>");
			sb.append("</table></form>");
			String restBody = sb.toString();

			restBody = restBody.replace("${TOKEN}", token == null ? "" : token);
			restBody = restBody.replace("${GEOMETRIES}",
					geometries == null ? "" : geometries);
			restBody = restBody.replace("${MAX_DEVIATION}", maxDeviation <= 0 ? "" : String.valueOf(maxDeviation));

			if (!(geometries == null)) {
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
			html.setTitle("Generalize");
			html
					.setCatalog(" &gt; <a href='"
							+ contextRoot
							+ "/rest/service/GeometryService'>Geometry Service</a> &gt; Generalize");
			html.setHeader("Generalize Geometries");
			html.setRestBody(restBody);
			result = html.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return result;
	}
}
